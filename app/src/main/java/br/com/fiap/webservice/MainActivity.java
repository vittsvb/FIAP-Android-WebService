package br.com.fiap.webservice;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView resposta = (TextView) findViewById(R.id.txt_resposta);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buscar(View view){
        //Instanciar a classe Task
        BuscaTask task = new BuscaTask();
        //Chamar o método execute
        task.execute(1);
    }

    private class BuscaTask extends AsyncTask<Integer,Void,String>{

        //Método executado após o método doInBackground
        @Override
        protected void onPostExecute(String s) {
            //Recuperar os valores do JSON
            if(s != null){
                try {
                    JSONObject json = new JSONObject(s);
                    long codigo = json.getLong("codigo");
                    String descricao = json.getString("descricao");
                    int quantidade = json.getInt("quantidade");
                    double preco = json.getDouble("preco");

                    resposta.setText("Código: " + codigo + "\nDescrição: " + descricao + "\nQuantidade: " + quantidade + "\nPreço: " + preco);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(MainActivity.this, "Erro ao realizar consulta", Toast.LENGTH_LONG).show();

            }
//            Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
        }

        //Método que executa a tarefa "pesada"
        @Override
        protected String doInBackground(Integer... params) {
            //Chamar o webservice restful
            try {
                //Criar a URL (localhost é 10.0.2.2)
                URL url = new URL("http://10.20.63.61:8080/MercadoFiap/rest/mercado/"+params[0]);
                //Obter uma conexão
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //Configurar a requisição
                //Método HTTP -> GET (Busca)
                connection.setRequestMethod("GET");
                //Tipo de dado que será devolvido pelo webservice (JSON)
                connection.setRequestProperty("Accept","application/json");

                //Status Code HTTP 200 OK ->Sucesso!
                if (connection.getResponseCode() == 200){
                    //Ler a resposta enviada pelo webservice
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

                    StringBuilder json = new StringBuilder();
                    String linha;
                    //Ler todas as linhas retornadas pelo ws
                    while ((linha = reader.readLine()) != null){
                        //Adiciona cada linha no builder
                        json.append(linha);
                    }
                    connection.disconnect();
                    return json.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
