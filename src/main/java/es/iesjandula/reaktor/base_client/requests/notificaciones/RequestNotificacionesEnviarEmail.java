package es.iesjandula.reaktor.base_client.requests.notificaciones;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.iesjandula.reaktor.base.utils.HttpClientUtils;
import es.iesjandula.reaktor.base_client.dtos.NotificationEmailDto;
import es.iesjandula.reaktor.base_client.security.service.AuthorizationService;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;

@Component
public class RequestNotificacionesEnviarEmail
{
	/**
	 * Logger of the class
	 */
	private static final Logger log = LoggerFactory.getLogger(RequestNotificacionesEnviarEmail.class);

	@Autowired
	private AuthorizationService authorizationService ;

	@Value("${reaktor.notifications_server_url}")
	private String notificationsServerUrl ;
	
	@Value("${reaktor.http_connection_timeout}")
	private int httpConnectionTimeout ;	

	/**
	 * Método - Enviar notificación email
	 * @param notificationEmailDto - DTO de la notificación email
	 * @throws BaseClientException con un error al enviar el email
	 */
	public void enviarNotificacionEmail(NotificationEmailDto notificationEmailDto) throws BaseClientException
	{	
		// Validamos los parámetros
		this.validarParametros(notificationEmailDto);

		// Enviamos la notificación email
		this.enviarNotificacionEmailInternal(notificationEmailDto);
	}

	/**
	 * Método - Validar parámetros
	 * @param notificationEmailDto - DTO de la notificación email
	 * @throws BaseClientException con un error al validar los parámetros
	 */
	private void validarParametros(NotificationEmailDto notificationEmailDto) throws BaseClientException
	{
		// Validamos si la instancia no es nula
		if (notificationEmailDto == null)
		{
			String errorMessage = "La notificación email es obligatoria";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_EMAIL, "La notificación email es obligatoria", null);
		}

		// Validamos los destinatarios
		if (notificationEmailDto.getTo() == null || notificationEmailDto.getTo().isEmpty())
		{
			String errorMessage = "Los destinatarios son obligatorios";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_EMAIL, "Los destinatarios son obligatorios", null);
		}

		// Validamos el asunto
		if (notificationEmailDto.getSubject() == null || notificationEmailDto.getSubject().isEmpty())
		{
			String errorMessage = "El asunto es obligatorio";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_EMAIL, "El asunto es obligatorio", null);
		}

		// Validamos el cuerpo
		if (notificationEmailDto.getBody() == null || notificationEmailDto.getBody().isEmpty())
		{
			String errorMessage = "El cuerpo es obligatorio";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_EMAIL, "El cuerpo es obligatorio", null);
		}	
	}

	/**
	 * Método - Enviar notificación email interno
	 * @param notificationEmailDto - DTO de la notificación email
	 * @throws BaseClientException con un error al enviar la notificación email
	 */
	private void enviarNotificacionEmailInternal(NotificationEmailDto notificationEmailDto) throws BaseClientException
	{
		// Creamos el HttpClient con timeout
		CloseableHttpClient closeableHttpClient = null ;
		CloseableHttpResponse closeableHttpResponse = null ;

		try
		{
			// Creamos el HttpClient con timeout
			closeableHttpClient = HttpClientUtils.crearHttpClientConTimeout(this.httpConnectionTimeout) ;

			// Logueamos
			log.debug("Comienzo del envío de notificación por email") ;
			
			// Configuración del HTTP POST con codificación UTF-8
			HttpPost httpPost = new HttpPost(this.notificationsServerUrl + "/notifications/email/") ;
			
			// Añadimos el token a la llamada
			httpPost.addHeader("Authorization", "Bearer " + this.authorizationService.obtenerTokenPersonalizado(this.httpConnectionTimeout)) ;

			// Indicamos que viaja un JSON
			httpPost.setHeader("Content-type", "application/json") ;

			// Nos aseguramos de que el ObjectMapper esté correctamente configurado
			ObjectMapper objectMapper = new ObjectMapper() ;
			
			// Nos aseguramos de que se registren automáticamente cualquier módulo de Jackson necesario
			objectMapper.findAndRegisterModules(); 
	
			// Serializamos el DTO de la notificación email a JSON asegurando UTF-8
			StringEntity entity = new StringEntity(objectMapper.writeValueAsString(notificationEmailDto), StandardCharsets.UTF_8) ;
			
			// Añadimos el body de la notificación email a la petición
			httpPost.setEntity(entity) ;
			
			// Enviamos la petición
			closeableHttpResponse = closeableHttpClient.execute(httpPost) ;
			
			// Logueamos
			log.debug("Fin del envío de notificación por email") ;

			// Comprobamos si la respuesta es OK
			if (closeableHttpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			{
				String errorMessage = "Error al enviar el email. " + 
									  "Código de error: " + closeableHttpResponse.getStatusLine().getStatusCode() + ". " +
									  "Motivo de error: " + closeableHttpResponse.getStatusLine().getReasonPhrase();
				
				log.error(errorMessage) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, null);
			}
			
			log.info("Notificación por email enviada correctamente") ;
		}
		catch (SocketTimeoutException socketTimeoutException)
		{
			String errorMessage = "SocketTimeoutException de lectura o escritura al comunicarse con el servidor (notificaciones por email)";
			
			log.error(errorMessage, socketTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, socketTimeoutException) ;
        }
		catch (ConnectTimeoutException connectTimeoutException)
		{
			String errorMessage = "ConnectTimeoutException al intentar conectar con el servidor (notificaciones por email)";

			log.error(errorMessage, connectTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, connectTimeoutException) ;
        }
		catch (IOException ioException)
		{	
			String errorMessage = "IOException mientras se enviaba la petición POST con la notificación por email";

			log.error(errorMessage, ioException) ;
			throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, ioException) ;
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
				String errorMessage = "IOException en closeableHttpResponse mientras se cerraba el flujo de datos (notificaciones por email)";

				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, ioException) ;
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
				String errorMessage = "IOException en closeableHttpClient mientras se cerraba el flujo de datos (notificaciones por email)";
				
				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, ioException) ;
			}
		}
	}
}
