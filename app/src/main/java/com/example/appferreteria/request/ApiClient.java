package com.example.appferreteria.request;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.appferreteria.modelo.CambiarPasswordRequest;
import com.example.appferreteria.modelo.LoginRequest;
import com.example.appferreteria.modelo.LoginResponse;
import com.example.appferreteria.modelo.MovimientoInventario;
import com.example.appferreteria.modelo.Notificacion;
import com.example.appferreteria.modelo.NotificacionRequest;
import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.modelo.ReporteVentas;
import com.example.appferreteria.modelo.TokenRequest;
import com.example.appferreteria.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ApiClient {
    public final static String BASE_URL = "http://192.168.1.4:5256/";

    public static final Gson gson = new GsonBuilder().setLenient().create();


    public static InmoServicio getInmoServicio() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(InmoServicio.class);
    }


    public interface InmoServicio {

        @FormUrlEncoded
        @POST("api/Usuarios/login")
        Call<LoginResponse> login(
                @Field("Usuario") String usuario,
                @Field("Clave") String clave
        );

        @GET("api/Usuarios")
        Call<Usuario> obtenerPerfil(@Header("Authorization") String token);

        @PUT("api/Usuarios/perfil")
        Call<Usuario> actualizarPerfil(
                @Header("Authorization") String token,
                @Body Usuario usuario
        );

        @POST("api/Usuarios/register")
        Call<Void> registerUsuario(
                @Header("Authorization") String token,
                @Body Usuario usuario
        );

        @PUT("api/Usuarios/cambiarPassword")
        Call<Void> changePassword(
                @Header("Authorization") String token,
                @Body CambiarPasswordRequest request
        );


        // ===========================
        // PRODUCTOS
        // ===========================


        @GET("api/productos")
        Call<List<Producto>> obtenerProductos();


        @Multipart
        @POST("api/productos/cargar")
        Call<Producto> cargarProducto(@Header("Authorization") String token,
                                      @Part MultipartBody.Part imagen,
                                      @Part("producto") RequestBody productoBody);


        @Multipart
        @PUT("api/productos/{id}")
        Call<Producto> actualizarProducto(
                @Header("Authorization") String token,
                @Path("id") int id,
                @Part MultipartBody.Part imagen,
                @Part("producto") RequestBody productoJson
        );


        @PUT("api/productos/baja/{id}")
        Call<Void> eliminarProducto(
                @Header("Authorization") String token,
                @Path("id") int id
        );


        // ===========================
        // Movimientos
        // ===========================

        @POST("api/movimientos")
        Call<ResponseBody> registrarMovimiento(@Header("Authorization") String token,
                                               @Body MovimientoInventario movimiento
        );

        @GET("api/movimientos")
        Call<List<MovimientoInventario>> listarMovimientos(
                @Header("Authorization") String token
        );

        // 2) GET: api/movimientos/producto/{codigoBarras}
        @GET("api/movimientos/producto/{codigoBarras}")
        Call<List<MovimientoInventario>> listarMovimientosPorProducto(
                @Header("Authorization") String token,
                @Path("codigoBarras") String codigoBarras
        );


        // ===========================
        // Informes ventas
        // ===========================
        @GET("api/reporteVentas")
        Call<List<ReporteVentas>> obtenerTodo(@Header("Authorization") String token);

        @GET("api/reporteVentas/rango")
        Call<List<ReporteVentas>> obtenerPorRango(
                @Header("Authorization") String token,
                @Query("fechaInicio") String fechaInicio,
                @Query("fechaFin") String fechaFin
        );


        // ===========================
        // Notificaciones
        // ===========================
        @POST("api/notificaciones/enviar-token")
        Call<Void> enviarToken(
                @Header("Authorization") String auth,
                @Body TokenRequest tokenRequest
        );


        @POST("api/notificaciones/enviar")
        Call<Void> enviarNotificaciones(@Body NotificacionRequest request);

        @GET("api/notificaciones")
        Call<List<Notificacion>> obtenerNotificaciones(
                @Header("Authorization") String token
        );


        @GET("api/notificaciones/{usuarioId}")
        Call<List<Notificacion>> traerNotificaciones(
                @Header("Authorization") String token,
                @Path("usuarioId") int usuarioId
        );

        // ===========================
        // Reseteo de contraseña
        // ===========================

        @FormUrlEncoded
        @POST("api/usuarios/email")
        Call<String> resetPassword(@Field("email") String email);


    }


    /*public static void guardarToken(Context context, String token, Usuario usuario) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.putString("rol", usuario.getRol());
        editor.putString("nombre", usuario.getNombre());
        editor.putString("apellido", usuario.getApellido());
        editor.apply();
    }*/


    /*public static String leerToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }*/

    // Acá fire base

    public static void guardarToken(Context context, LoginResponse login) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", login.getToken());
        editor.putString("rol", login.getRol());
        editor.putString("nombre", login.getNombre());
        editor.putString("apellido", login.getApellido());
        editor.putInt("usuarioId", login.getUsuarioId());
        editor.apply();
    }

    public static String leerToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        return sp.getString("token", "");
    }

    public static int obtenerUsuarioId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        return sp.getInt("usuarioId", -1);
    }




}
