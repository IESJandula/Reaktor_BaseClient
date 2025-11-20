package es.iesjandula.reaktor.base_client.requests.firebase;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.iesjandula.reaktor.base.security.models.DtoUsuarioBase;
import es.iesjandula.reaktor.base.utils.HttpClientUtils;
import es.iesjandula.reaktor.base_client.security.service.AuthorizationService;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RequestFirebaseObtenerUsuarios
{
	/**
	 * Logger of the class
	 */
	private static final Logger log = LoggerFactory.getLogger(RequestFirebaseObtenerUsuarios.class);

	@Autowired
	private AuthorizationService authorizationService ; 

    @Value("${reaktor.firebase_server_url}")
    private String firebaseUrl;

    @Value("${reaktor.http_connection_timeout}")
    private int httpConnectionTimeout;

    public List<DtoUsuarioBase> obtenerUsuarios() throws BaseClientException
    {
        // Lista de usuarios
        List<DtoUsuarioBase> usuarios = null ;

		// Creamos el HttpClient con timeout
		CloseableHttpClient closeableHttpClient = null ;
		CloseableHttpResponse closeableHttpResponse = null ;

		try
		{
			// Creamos el HttpClient con timeout
			closeableHttpClient = HttpClientUtils.crearHttpClientConTimeout(this.httpConnectionTimeout) ;

			// Logueamos
			log.debug("Comienzo de la obtención de usuarios de Firebase") ;
			
			// Configuración del HTTP GET con codificación UTF-8
			HttpGet httpGet = new HttpGet(this.firebaseUrl + "/firebase/queries/users") ;
			
			// Añadimos el token a la llamada
			httpGet.addHeader("Authorization", "Bearer " + this.authorizationService.obtenerTokenPersonalizado(this.httpConnectionTimeout)) ;

			// Enviamos la petición
			closeableHttpResponse = closeableHttpClient.execute(httpGet) ;

			// Logueamos
			log.debug("Fin de la obtención de usuarios de Firebase") ;

			// Comprobamos si la respuesta es OK
			if (closeableHttpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			{
				String errorMessage = "Error al obtener los usuarios de Firebase. " + 
									  "Código de error: " + closeableHttpResponse.getStatusLine().getStatusCode() + ". " +
									  "Motivo de error: " + closeableHttpResponse.getStatusLine().getReasonPhrase();
				
				log.error(errorMessage) ;
				throw new BaseClientException(BaseClientConstants.ERR_GETTING_USERS, errorMessage, null);
			}

            // Creamos el ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper() ;

            // Convertimos la respuesta a DtoUsuarioBase
            usuarios = objectMapper.readValue(EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8), new TypeReference<List<DtoUsuarioBase>>() {}) ;

            // Logueamos los usuarios obtenidos
            log.info("Fin de la obtención de usuarios de Firebase con descarga de " + (usuarios != null && !usuarios.isEmpty() ? usuarios.size() : 0) + " usuarios") ;

            // Devolvemos la lista de usuarios
            return usuarios ;
		}
		catch (SocketTimeoutException socketTimeoutException)
		{
			String errorMessage = "SocketTimeoutException de lectura o escritura al comunicarse con el servidor (obtención de usuarios de Firebase)";
			
			log.error(errorMessage, socketTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_GETTING_USERS, errorMessage, socketTimeoutException) ;
        }
		catch (ConnectTimeoutException connectTimeoutException)
		{
			String errorMessage = "ConnectTimeoutException al intentar conectar con el servidor (obtención de usuarios de Firebase)";

			log.error(errorMessage, connectTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_GETTING_USERS, errorMessage, connectTimeoutException) ;
        }
		catch (IOException ioException)
		{	
			String errorMessage = "IOException mientras se enviaba la petición POST con la obtención de usuarios de Firebase";

			log.error(errorMessage, ioException) ;
			throw new BaseClientException(BaseClientConstants.ERR_GETTING_USERS, errorMessage, ioException) ;
		}
		finally
		{
			try
			{
				if (closeableHttpResponse != null)
				{
					closeableHttpResponse.close() ;
				}
			}
			catch (IOException ioException)
			{
				String errorMessage = "IOException en closeableHttpResponse mientras se cerraba el flujo de datos (obtención de usuarios de Firebase)";

				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_GETTING_USERS, errorMessage, ioException) ;
			}

			try
			{
				if (closeableHttpClient != null)
				{
					closeableHttpClient.close() ;
				}
			}
			catch (IOException ioException)
			{
				String errorMessage = "IOException en closeableHttpClient mientras se cerraba el flujo de datos (obtención de usuarios de Firebase)";
				
				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_GETTING_USERS, errorMessage, ioException) ;
			}
		}
    }
}
