package es.iesjandula.reaktor.base_client.requests.school_base_server;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.iesjandula.reaktor.base.utils.BaseException;
import es.iesjandula.reaktor.base.utils.HttpClientUtils;
import es.iesjandula.reaktor.base_client.security.service.AuthorizationService;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;

@Component
public class ObtencionCursoAcademico
{
	/**
	 * Logger of the class
	 */
	private static final Logger log = LoggerFactory.getLogger(ObtencionCursoAcademico.class);

	@Autowired
	private AuthorizationService authorizationService ;

	@Value("${reaktor.school_base_server_url}")
	private String schoolBaseServerUrl ;
	
	@Value("${reaktor.http_connection_timeout}")
	private int httpConnectionTimeout ;	

	/**
	 * Método - Obtener curso académico seleccionado
	 * @return String curso académico seleccionado
	 * @throws BaseException error al obtener el token personalizado
	 * @throws BaseClientException con un error al obtener el curso académico seleccionado
	 */
	public String obtenerCursoAcademicoSeleccionado() throws BaseException, BaseClientException
	{		
		// Creamos el HttpClient con timeout
		CloseableHttpClient closeableHttpClient = null ;
		CloseableHttpResponse closeableHttpResponse = null ;

		try
		{
			// Creamos el HttpClient con timeout
			closeableHttpClient = HttpClientUtils.crearHttpClientConTimeout(this.httpConnectionTimeout) ;

			// Configuración del HTTP GET con codificación UTF-8
			HttpGet httpGet = new HttpGet(this.schoolBaseServerUrl + "/school_base_server/curso_academico/") ;

			// Añadimos el token a la llamada
			httpGet.addHeader("Authorization", "Bearer " + this.authorizationService.obtenerTokenPersonalizado(this.httpConnectionTimeout)) ;
				
			// Enviamos la petición
			closeableHttpResponse = closeableHttpClient.execute(httpGet) ;

			// Logueamos
			log.debug("Fin de la obtención del curso académico seleccionado") ;

			// Verificamos el estado de la respuesta HTTP
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode() ;
			
			if (statusCode != 200)
			{
				String errorMessage = "Error al obtener el curso académico seleccionado. " + 
									  "Código de error: " + closeableHttpResponse.getStatusLine().getStatusCode() + ". " +
									  "Motivo de error: " + closeableHttpResponse.getStatusLine().getReasonPhrase();
				
				log.error(errorMessage) ;
				throw new BaseClientException(BaseClientConstants.ERR_GETTING_CURSO_ACADEMICO_SELECTED, errorMessage, null);
			}
            
            // Convertimos la respuesta a String
            String cursoAcademico = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);

            // Devolvemos el curso académico seleccionado
            return cursoAcademico;
		}
		catch (SocketTimeoutException socketTimeoutException)
		{
			String errorMessage = "SocketTimeoutException de lectura o escritura al comunicarse con el servidor (obtención del curso académico seleccionado)";
			
			log.error(errorMessage, socketTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_GETTING_CURSO_ACADEMICO_SELECTED, errorMessage, socketTimeoutException) ;
        }
		catch (ConnectTimeoutException connectTimeoutException)
		{
			String errorMessage = "ConnectTimeoutException al intentar conectar con el servidor (obtención del curso académico seleccionado)";

			log.error(errorMessage, connectTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_GETTING_CURSO_ACADEMICO_SELECTED, errorMessage, connectTimeoutException) ;
        }
		catch (IOException ioException)
		{	
			String errorMessage = "IOException mientras se enviaba la petición GET con la obtención del curso académico seleccionado)";

			log.error(errorMessage, ioException) ;
			throw new BaseClientException(BaseClientConstants.ERR_GETTING_CURSO_ACADEMICO_SELECTED, errorMessage, ioException) ;
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
				String errorMessage = "IOException en closeableHttpResponse mientras se cerraba el flujo de datos (obtención del curso académico seleccionado)";

				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_GETTING_CURSO_ACADEMICO_SELECTED, errorMessage, ioException) ;
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
				String errorMessage = "IOException en closeableHttpClient mientras se cerraba el flujo de datos (creación de notificación web)";
				
				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, ioException) ;
			}
		}
	}
}
