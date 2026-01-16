package es.iesjandula.reaktor.base_client.requests.notificaciones;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.iesjandula.reaktor.base.utils.HttpClientUtils;
import es.iesjandula.reaktor.base_client.dtos.NotificationWebDto;
import es.iesjandula.reaktor.base_client.security.service.AuthorizationService;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;

@Component
public class RequestNotificacionesEnviarWeb
{
	/**
	 * Logger of the class
	 */
	private static final Logger log = LoggerFactory.getLogger(RequestNotificacionesEnviarWeb.class);

	@Autowired
	private AuthorizationService authorizationService ;

	@Value("${reaktor.notifications_server_url}")
	private String notificationsServerUrl ;
	
	@Value("${reaktor.http_connection_timeout}")
	private int httpConnectionTimeout ;	

	/**
	 * Método - Enviar notificación web
	 * @param notificationWebDto - DTO de la notificación web
	 * @return Integer identificador de la notificación web
	 * @throws BaseClientException con un error al enviar la notificación web
	 */
	public Integer enviarNotificacionWeb(NotificationWebDto notificationWebDto) throws BaseClientException
	{
		// Validamos los parámetros
		this.validarParametros(notificationWebDto);
		
		// Enviamos la notificación web y devolvemos el identificador de la notificación web
		return this.enviarNotificacionWebInternal(notificationWebDto);
	}

	/**
	 * Método - Validar parámetros
	 * @param notificationWebDto - DTO de la notificación web
	 * @throws BaseClientException con un error al validar los parámetros
	 */
	private void validarParametros(NotificationWebDto notificationWebDto) throws BaseClientException
	{
		// Validamos si la instancia no es nula
		if (notificationWebDto == null)
		{
			String errorMessage = "La notificación web es obligatoria";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_WEB, "La notificación web es obligatoria", null);
		}

		// Validamos el texto
		if (notificationWebDto.getTexto() == null || notificationWebDto.getTexto().isEmpty())
		{
			String errorMessage = "El texto es obligatorio";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_WEB, "El texto es obligatorio", null);
		}

		// Validamos la fecha de inicio
		if (notificationWebDto.getFechaInicio() == null || notificationWebDto.getFechaInicio().isEmpty())
		{
			String errorMessage = "La fecha de inicio es obligatoria";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_WEB, "La fecha de inicio es obligatoria", null);
		}

		// Validamos la hora de inicio
		if (notificationWebDto.getHoraInicio() == null || notificationWebDto.getHoraInicio().isEmpty())
		{
			String errorMessage = "La hora de inicio es obligatoria";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_WEB, "La hora de inicio es obligatoria", null);
		}

		// Validamos la fecha de fin
		if (notificationWebDto.getFechaFin() == null || notificationWebDto.getFechaFin().isEmpty())
		{
			String errorMessage = "La fecha de fin es obligatoria";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_WEB, "La fecha de fin es obligatoria", null);
		}

		// Validamos la hora de fin
		if (notificationWebDto.getHoraFin() == null || notificationWebDto.getHoraFin().isEmpty())
		{
			String errorMessage = "La hora de fin es obligatoria";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_WEB, "La hora de fin es obligatoria", null);
		}

		// Validamos el receptor
		if (notificationWebDto.getReceptor() == null || notificationWebDto.getReceptor().isEmpty())
		{
			String errorMessage = "El receptor es obligatorio";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_WEB, "El receptor es obligatorio", null);
		}

		// Validamos el tipo
		if (notificationWebDto.getTipo() == null || notificationWebDto.getTipo().isEmpty())
		{
			String errorMessage = "El tipo es obligatorio";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_INVALID_PARAMETER_NOTIFICATION_WEB, "El tipo es obligatorio", null);
		}
	}

	/**
	 * Método - Enviar notificación web interno
	 * @param notificationWebDto - DTO de la notificación web
	 * @return Integer identificador de la notificación web
	 * @throws BaseClientException con un error al enviar la notificación web
	 */
	private Integer enviarNotificacionWebInternal(NotificationWebDto notificationWebDto) throws BaseClientException
	{
		// Creamos el HttpClient con timeout
		CloseableHttpClient closeableHttpClient = null ;
		CloseableHttpResponse closeableHttpResponse = null ;

		try
		{
			// Creamos el HttpClient con timeout
			closeableHttpClient = HttpClientUtils.crearHttpClientConTimeout(this.httpConnectionTimeout) ;

			// Logueamos
			log.debug("Comienzo del envío de notificación web") ;
			
			// Configuración del HTTP POST con codificación UTF-8
			HttpPost httpPost = new HttpPost(this.notificationsServerUrl + "/notifications/web/apps") ;

			// Añadimos 
			httpPost.addHeader(BaseClientConstants.PARAM_TEXTO_NOTIFICACION, notificationWebDto.getTexto());
			httpPost.addHeader(BaseClientConstants.PARAM_FECHA_INICIO_NOTIFICACION, notificationWebDto.getFechaInicio());
			httpPost.addHeader(BaseClientConstants.PARAM_HORA_INICIO_NOTIFICACION, notificationWebDto.getHoraInicio());
			httpPost.addHeader(BaseClientConstants.PARAM_FECHA_FIN_NOTIFICACION, notificationWebDto.getFechaFin());
			httpPost.addHeader(BaseClientConstants.PARAM_HORA_FIN_NOTIFICACION, notificationWebDto.getHoraFin());
			httpPost.addHeader(BaseClientConstants.PARAM_RECEPTOR_NOTIFICACION, notificationWebDto.getReceptor());
			httpPost.addHeader(BaseClientConstants.PARAM_TIPO_NOTIFICACION, notificationWebDto.getTipo());
			
			// Añadimos el token a la llamada
			httpPost.addHeader("Authorization", "Bearer " + this.authorizationService.obtenerTokenPersonalizado(this.httpConnectionTimeout)) ;
				
			// Enviamos la petición
			closeableHttpResponse = closeableHttpClient.execute(httpPost) ;

			// Logueamos
			log.debug("Fin del envío de notificación web") ;

			// Verificamos el estado de la respuesta HTTP
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode() ;
			
			if (statusCode != 200)
			{
				String errorMessage = "Error al enviar la notificación web. " + 
									  "Código de error: " + closeableHttpResponse.getStatusLine().getStatusCode() + ". " +
									  "Motivo de error: " + closeableHttpResponse.getStatusLine().getReasonPhrase();
				
				log.error(errorMessage) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, null);
			}

			// Obtenemos el identificador de la notificación web
			Integer idNotificacion = Integer.valueOf(EntityUtils.toString(closeableHttpResponse.getEntity()));
			
			// Logueamos
			log.info("Notificación web enviada correctamente con identificador: " + idNotificacion) ;

			// Devolvemos el identificador de la notificación web
			return idNotificacion;
		}
		catch (SocketTimeoutException socketTimeoutException)
		{
			String errorMessage = "SocketTimeoutException de lectura o escritura al comunicarse con el servidor (creación de notificación web)";
			
			log.error(errorMessage, socketTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, socketTimeoutException) ;
        }
		catch (ConnectTimeoutException connectTimeoutException)
		{
			String errorMessage = "ConnectTimeoutException al intentar conectar con el servidor (creación de notificación web)";

			log.error(errorMessage, connectTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, connectTimeoutException) ;
        }
		catch (IOException ioException)
		{	
			String errorMessage = "IOException mientras se enviaba la petición POST con la notificación web (creación de notificación web)";

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
				String errorMessage = "IOException en closeableHttpResponse mientras se cerraba el flujo de datos (creación de notificación web)";

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
				String errorMessage = "IOException en closeableHttpClient mientras se cerraba el flujo de datos (creación de notificación web)";
				
				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, ioException) ;
			}
		}
	}

	/**
	 * Método - Eliminar notificación web
	 * @param idNotificacion - Identificador de la notificación web
	 * @throws BaseClientException con un error al eliminar la notificación web
	 */
	public void eliminarNotificacionWeb(Integer idNotificacion) throws BaseClientException
	{
		// Creamos el HttpClient con timeout
		CloseableHttpClient closeableHttpClient = null ;
		CloseableHttpResponse closeableHttpResponse = null ;

		try
		{
			// Creamos el HttpClient con timeout
			closeableHttpClient = HttpClientUtils.crearHttpClientConTimeout(this.httpConnectionTimeout) ;

			// Configuración del HTTP DELETE con codificación UTF-8
			HttpDelete httpDelete = new HttpDelete(this.notificationsServerUrl + "/notifications/web/apps/" + idNotificacion) ;

			// Añadimos el token a la llamada
			httpDelete.addHeader("Authorization", "Bearer " + this.authorizationService.obtenerTokenPersonalizado(this.httpConnectionTimeout)) ;

			// Enviamos la petición
			closeableHttpResponse = closeableHttpClient.execute(httpDelete) ;

			// Logueamos
			log.debug("Fin de la eliminación de notificación web") ;

			// Verificamos el estado de la respuesta HTTP
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode() ;
			
			if (statusCode != 200)
			{
				String errorMessage = "Error al eliminar la notificación web. " + 
									  "Código de error: " + closeableHttpResponse.getStatusLine().getStatusCode() + ". " +
									  "Motivo de error: " + closeableHttpResponse.getStatusLine().getReasonPhrase();
				
				log.error(errorMessage) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, null);
			}

			// Logueamos
			log.info("Notificación web eliminada correctamente con identificador: " + idNotificacion) ;
		}
		catch (SocketTimeoutException socketTimeoutException)
		{
			String errorMessage = "SocketTimeoutException de lectura o escritura al comunicarse con el servidor (eliminación de notificación web)";
			
			log.error(errorMessage, socketTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, socketTimeoutException) ;
		}
		catch (IOException ioException)
		{
			String errorMessage = "IOException mientras se enviaba la petición DELETE con la notificación web (eliminación de notificación web)";
			
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
				String errorMessage = "IOException en closeableHttpResponse mientras se cerraba el flujo de datos (eliminación de notificación web)";
				
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
				String errorMessage = "IOException en closeableHttpClient mientras se cerraba el flujo de datos (eliminación de notificación web)";
				
				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, ioException) ;
			}
		}
	}
}
