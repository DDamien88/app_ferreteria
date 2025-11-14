package com.example.appferreteria;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.appferreteria.LoginActivity;
import com.example.appferreteria.modelo.Usuario;
import com.example.appferreteria.request.ApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appferreteria.databinding.ActivityMainBinding;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        //User logueado
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();
        String token = ApiClient.leerToken(this);

        Call<Usuario> callPerfil = api.obtenerPerfil("Bearer " + token);
        callPerfil.enqueue(new retrofit2.Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, retrofit2.Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();

                    NavigationView navigationView = binding.navView;
                    View headerView = navigationView.getHeaderView(0);
                    TextView tvNombreUsuario = headerView.findViewById(R.id.idUser);
                    TextView tvEmailUsuario = headerView.findViewById(R.id.tvEmail);

                    tvNombreUsuario.setText(usuario.getNombre() + " " + usuario.getApellido());
                    tvEmailUsuario.setText(usuario.getEmail());
                } else {
                    Log.e("MainActivity", "Error al obtener propietario: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("MainActivity", "Fallo de conexión: " + t.getMessage());
            }
        });


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.fragmentInicio, R.id.scrollPerfil, R.id.fragmentProductos, R.id.nav_slideshow, R.id.fragmentAjuste, R.id.fragmentReportes, R.id.historialMovimientosFragment)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //Solo ver "Registrar usuarios"
        //NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem registrarUsuario = menu.findItem(R.id.registrarUsuarioFragment);

// Leer rol desde SharedPreferences
        SharedPreferences sp = getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        String rol = sp.getString("rol", "");

// Mostrar solo si es Dueño
        registrarUsuario.setVisible("Dueño".equals(rol));


        //botón flotante
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            FloatingActionButton fab = findViewById(R.id.fab);

           /* if (destination.getId() == R.id.nav_inmuebles) {
                fab.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.plus);
                fab.setOnClickListener(v -> {
                    navController.navigate(R.id.agregarInmuebleFragment);
                });
            } else {*/
            fab.setVisibility(View.GONE);
            //}
        });


        //cerrar sesión

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Deseás cerrar la sesión actual?")
                        .setPositiveButton("Sí", (dialog, which) -> cerrarSesion())
                        .setNegativeButton("Cancelar", null)
                        .show();

                drawer.closeDrawers();
                return true;
            }
            // Dejar que NavigationUI maneje los otros items
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                binding.drawerLayout.closeDrawers();
            }
            return handled;
        });
    }

    private void cerrarSesion() {
        // Borrar token y datos del usuario
        SharedPreferences sp = getSharedPreferences("token.xml", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear(); // borra todo
        editor.apply();

        // Redirigir al LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}