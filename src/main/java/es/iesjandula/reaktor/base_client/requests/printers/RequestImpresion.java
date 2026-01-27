package es.iesjandula.reaktor.base_client.requests.printers;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.iesjandula.reaktor.base.utils.HttpClientUtils;
import es.iesjandula.reaktor.base_client.security.service.AuthorizationService;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;

@Component
public class RequestImpresion
{
	/**
	 * Logger of the class
	 */
	private static final Logger log = LoggerFactory.getLogger(RequestImpresion.class);

	@Autowired
	private AuthorizationService authorizationService ;

	@Value("${reaktor.printers_server_url}")
	private String printersServerUrl ;
	
	@Value("${reaktor.http_connection_timeout}")
	private int httpConnectionTimeout ;	

	/**
	 * Método - Imprimir PDF
	 * @param pdf - PDF a imprimir
	 * @return Integer identificador de la impresión
	 * @throws BaseClientException con un error al imprimir el PDF
	 */
	public void imprimirPdf(byte[] pdf) throws BaseClientException
	{
		// Validamos los parámetros
		this.validarParametros(pdf);
		
		// Imprimimos el PDF
		this.imprimirPdfInternal(pdf);
	}

	/**
	 * Método - Validar parámetros
	 * @param pdf - PDF a imprimir
	 * @throws BaseClientException con un error al validar los parámetros
	 */
	private void validarParametros(byte[] pdf) throws BaseClientException
	{
		// Validamos si la instancia no es nula
		if (pdf == null)
		{
			String errorMessage = "El PDF es obligatorio";

			log.error(errorMessage) ;
			throw new BaseClientException(BaseClientConstants.ERR_PRINTING_PDF, "El PDF es obligatorio", null);
		}
	}

	/**
	 * Método - Imprimir PDF interno
	 * @param pdf - PDF a imprimir
	 * @throws BaseClientException con un error al imprimir el PDF
	 */
	private void imprimirPdfInternal(byte[] pdf) throws BaseClientException
	{
		// Creamos el HttpClient con timeout
		CloseableHttpClient closeableHttpClient = null ;
		CloseableHttpResponse closeableHttpResponse = null ;

		try
		{
			// Creamos el HttpClient con timeout
			closeableHttpClient = HttpClientUtils.crearHttpClientConTimeout(this.httpConnectionTimeout) ;

			// Logueamos
			log.debug("Comienzo de la impresión del PDF") ;
			
			// Configuración del HTTP POST con codificación UTF-8
			HttpPost httpPost = new HttpPost(this.printersServerUrl + "/printers/web/print/app") ;

            // Añadimos el content-type
            httpPost.addHeader("Content-Type", "application/pdf");

            // Creamos el multipart
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            // Añadimos el fichero
            builder.addBinaryBody("file", pdf, ContentType.APPLICATION_OCTET_STREAM, "documento.pdf") ;

            // Añadimos el multipart a la petición
            httpPost.setEntity(builder.build()) ;
			
			// Añadimos el token a la llamada
			httpPost.addHeader("Authorization", "Bearer " + this.authorizationService.obtenerTokenPersonalizado(this.httpConnectionTimeout)) ;
				
			// Enviamos la petición
			closeableHttpResponse = closeableHttpClient.execute(httpPost) ;

			// Logueamos
			log.debug("Fin de la impresión del PDF") ;

			// Verificamos el estado de la respuesta HTTP
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode() ;
			
			if (statusCode != 200)
			{
				String errorMessage = "Error al imprimir el PDF. " + 
									  "Código de error: " + closeableHttpResponse.getStatusLine().getStatusCode() + ". " +
									  "Motivo de error: " + closeableHttpResponse.getStatusLine().getReasonPhrase();
				
				log.error(errorMessage) ;
				throw new BaseClientException(BaseClientConstants.ERR_PRINTING_PDF, errorMessage, null);
			}            
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
}
