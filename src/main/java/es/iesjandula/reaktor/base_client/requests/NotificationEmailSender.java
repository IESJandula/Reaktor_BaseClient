package es.iesjandula.reaktor.base_client.requests;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.iesjandula.reaktor.base.utils.HttpClientUtils;
import es.iesjandula.reaktor.base_client.security.service.AuthorizationService;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationEmailSender
{
	@Autowired
	private AuthorizationService authorizationService ;

	@Value("${reaktor.notifications_server_url}")
	private String notificationsServerUrl ;
	
	@Value("${reaktor.http_connection_timeout}")
	private int httpConnectionTimeout ;	

	

	/**
	 * Método - Enviar email
	 * @param email - Email del destinatario
	 * @param subject - Asunto del email
	 * @param body      - Cuerpo del email
	 * @throws BaseClientException con un error al enviar el email
	 */
	public void send(String email, String subject, String body) throws BaseClientException
	{	
		CloseableHttpClient closeableHttpClient = null ;
		CloseableHttpResponse closeableHttpResponse = null ;

		try
		{
			// Creamos el HttpClient con timeout
			closeableHttpClient = HttpClientUtils.crearHttpClientConTimeout(this.httpConnectionTimeout) ;

			// Logueamos
			log.debug("SEND_EMAIL - POST - Inicio Método - Enviar email") ;
			
			// Configuración del HTTP POST con codificación UTF-8
			HttpPost httpPost = new HttpPost(this.notificationsServerUrl + "/notifications/email") ;
			
			// Añadimos el token a la llamada
			httpPost.addHeader("Authorization", "Bearer " + this.authorizationService.obtenerTokenPersonalizado(this.httpConnectionTimeout)) ;
			
			log.debug("SEND_EMAIL - POST - Envío - Enviar email") ;
	
			// Enviamos la petición
			closeableHttpResponse = closeableHttpClient.execute(httpPost) ;

			// Comprobamos si la respuesta es OK
			if (closeableHttpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			{
				String errorMessage = "Error al enviar el email. " + 
									  "Código de error: " + closeableHttpResponse.getStatusLine().getStatusCode() + ". " +
									  "Motivo de error: " + closeableHttpResponse.getStatusLine().getReasonPhrase();
				
				log.error(errorMessage) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, null);
			}
			
			log.info("SEND_EMAIL - FIN - Enviar email") ;
		}
		catch (SocketTimeoutException socketTimeoutException)
		{
			String errorMessage = "SocketTimeoutException de lectura o escritura al comunicarse con el servidor (info impresoras)";
			
			log.error(errorMessage, socketTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, socketTimeoutException) ;
        }
		catch (ConnectTimeoutException connectTimeoutException)
		{
			String errorMessage = "ConnectTimeoutException al intentar conectar con el servidor (info impresoras)";

			log.error(errorMessage, connectTimeoutException) ;
			throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, connectTimeoutException) ;
        }
		catch (IOException ioException)
		{	
			String errorMessage = "IOException mientras se enviaba la petición POST con el email";

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
				String errorMessage = "IOException en closeableHttpResponse mientras se cerraba el flujo de datos";

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
				String errorMessage = "IOException en closeableHttpClient mientras se cerraba el flujo de datos";
				
				log.error(errorMessage, ioException) ;
				throw new BaseClientException(BaseClientConstants.ERR_SENDING_EMAIL, errorMessage, ioException) ;
			}
		}
	}
}
