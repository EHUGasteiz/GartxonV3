package com.example.proyecto2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto2.Dialogs.Idioma;
import com.example.proyecto2.Services.ActualizarTokenBDService;
import com.example.proyecto2.Services.ObtenerPerfilBDService;
import com.example.proyecto2.Services.ServicioFirebase;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class ActividadPrincipal extends AppCompatActivity implements InterfaceIdioma{

    DrawerLayout elmenudesplegable;
    String email;
    String idiomaApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos los parametros que recibe del login o del idioma
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email= extras.getString("email");
            idiomaApp = extras.getString("idioma");
        }

        String token = null;
        try {
            File file = new File(getFilesDir(), "token.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                token = reader.readLine();
                reader.close();
                file.delete();
            }
        } catch (IOException e) {
        }

        if (token != null) {
            //Creamos un objeto de tipo data y le metemos el email y el token para actualizarlo
            Data datos = new Data.Builder()
                    .putString("email", email)
                    .putString("token", token)
                    .build();

            //Creamos una solicitud de trabajo para la ejecucion de la llamada asincrona a la bd
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ActualizarTokenBDService.class)
                    .setInputData(datos)
                    .build();

            //Le añadimos un observable para que actue una vez reciba de vuelta algo
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if(workInfo != null && workInfo.getState().isFinished()){

                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        }

        if (idiomaApp != null) {
            //Cambiamos el idioma elegido por el usuario
            Locale nuevaloc = new Locale(idiomaApp);
            Locale.setDefault(nuevaloc);
            Configuration configuration = getBaseContext().getResources().getConfiguration();
            configuration.setLocale(nuevaloc);
            configuration.setLayoutDirection(nuevaloc);
            Context context = getBaseContext().createConfigurationContext(configuration);
            getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        }

        setContentView(R.layout.activity_main);

        //Ponemos nuestra toolbar como action bar
        setSupportActionBar(findViewById(R.id.labarra));

        elmenudesplegable = findViewById(R.id.drawer_layout);

        //Obtenemos el navigation view y le decimos que hacer en funcion del item seleccionado
        NavigationView elnavigation = findViewById(R.id.elnavigationview);
        elnavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        //Navegamos utilizando el nav_graph. Por defecto tal y como lo tenemos definido al abrir la aplicacion sale este
                        Bundle bundle = new Bundle();
                        bundle.putString("tipo", "todos");

                        Navigation.findNavController(ActividadPrincipal.this, R.id.nav_host_fragment).navigate(R.id.fragmentPrincipal, bundle);
                        break;
                    case R.id.movil:
                        Bundle bundleMovil = new Bundle();
                        bundleMovil.putString("tipo", "movil");

                        Navigation.findNavController(ActividadPrincipal.this, R.id.nav_host_fragment).navigate(R.id.fragmentPrincipal, bundleMovil);
                        break;
                    case R.id.ordenador:
                        Bundle bundleOrdenador = new Bundle();
                        bundleOrdenador.putString("tipo", "ordenador");

                        Navigation.findNavController(ActividadPrincipal.this, R.id.nav_host_fragment).navigate(R.id.fragmentPrincipal, bundleOrdenador);
                        break;
                    case R.id.consola:
                        Bundle bundleConsola = new Bundle();
                        bundleConsola.putString("tipo", "consola");

                        Navigation.findNavController(ActividadPrincipal.this, R.id.nav_host_fragment).navigate(R.id.fragmentPrincipal, bundleConsola);
                        break;
                    case R.id.otros:
                        Bundle bundleOtros = new Bundle();
                        bundleOtros.putString("tipo", "otros");

                        Navigation.findNavController(ActividadPrincipal.this, R.id.nav_host_fragment).navigate(R.id.fragmentPrincipal, bundleOtros);
                        break;
                    case R.id.perfil:
                        Bundle bundlePerfil = new Bundle();
                        bundlePerfil.putString("email", email);

                        Navigation.findNavController(ActividadPrincipal.this, R.id.nav_host_fragment).navigate(R.id.fragmentPerfil, bundlePerfil);
                        break;
                    case R.id.idioma:
                        DialogFragment newFragment = new Idioma();
                        newFragment.show(getSupportFragmentManager(), "SeleccionContenidoPrincipal");
                        break;
                    case R.id.preferencias:
                        Navigation.findNavController(ActividadPrincipal.this, R.id.nav_host_fragment).navigate(R.id.fragmentPreferencias);
                        break;
                }
                elmenudesplegable.closeDrawers();
                return false;
            }
        });

        //Para que el navigation view se abra al pulsar el icono y al darle atras se cierre
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Asigna el fichero xml con la definicion del menu toolbar
        getMenuInflater().inflate(R.menu.toolbar_opciones,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Para utilizar la opciones del toolbar
        switch(item.getItemId()) {
            case android.R.id.home:
                elmenudesplegable.openDrawer(GravityCompat.START);
                break;
            case R.id.logout:
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Para cerrar el navigation view
        if (elmenudesplegable.isDrawerOpen(GravityCompat.START)) {
            elmenudesplegable.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        //Cuando se rota guarda email
        super.onSaveInstanceState(outState);
        outState.putString("email",  email);
    }

    @Override
    public void cambiarIdioma(String idioma) {
        //Cuando se selecciona un idioma se carga y envia de nuevo a la pagina principal
        idiomaApp = idioma;
        getIntent().putExtra("idioma" , idiomaApp);
        getIntent().putExtra("email", email);
        finish();
        startActivity(getIntent());
    }

    public String obtenerUsuario() {
        return email;
    }

}
