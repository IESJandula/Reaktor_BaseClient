package es.iesjandula.reaktor.base_client.security.service ;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.iesjandula.reaktor.base.security.service.PublicKeyGetter;
import es.iesjandula.reaktor.base.utils.BaseException;
import es.iesjandula.reaktor.base.utils.HttpClientUtils;
import es.iesjandula.reaktor.base_client.utils.BaseClientConstants;
import es.iesjandula.reaktor.base_client.utils.BaseClientException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Service
public class AuthorizationService
{
    /**
     * Logger of the class
     */
    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

	@Value("${reaktor.firebase_server_url}")
	private String firebaseServerUrl ;
	
    @Value("${reaktor.clientId}")
    private String clientId;

	@Autowired
	private SessionStorageService sessionStorageService ;
	
	/** Atributo - JWT Parser */
	private JwtParser jwtParser ;
	
	@Autowired
	private PublicKeyGetter publicKeyGetter ;
	
	/**
	 * Inicializa la instancia de JWT Parser
	 * @throws BaseClientException con un error al leer la clave pública
	 */
	@PostConstruct
	public void init() throws BaseException
	{
		this.jwtParser = Jwts.parser() 								 				 // Inicializa el parser (analizador) de JWT
                			 .verifyWith(this.publicKeyGetter.obtenerClavePublica()) // Configura la clave pública para validar la firma del JWT
                			 .build() ; 							 				 // Construye el objeto del parser configurado
	}
	
	/**
	 * Realiza una solicitud HTTP POST al otro microservicio para obtener un token personalizado
	 *
	 * @param timeout
	 * @return El token JWT obtenido del microservicio
	 * @throws BaseClientException error al obtener el token
	 */
	public String obtenerTokenPersonalizado(int timeout) throws BaseClientException
	{
	    // Verificamos si ya tenemos un token en "sesión"
		String token = this.sessionStorageService.getToken() ;

		if (token == null || this.tokenExpirado(token))
		{
			CloseableHttpClient closeableHttpClient 	= HttpClientUtils.crearHttpClientConTimeout(timeout) ;
		    CloseableHttpResponse closeableHttpResponse = null ;

		    try
		    {
                // Creamos una solicitud HTTP POST a nuestro microservicio Firebase
                HttpPost postRequest = new HttpPost(this.firebaseServerUrl + "/firebase/token/app") ;

                // Configuramos los headers con el CLIENT-ID
                postRequest.addHeader("X-CLIENT-ID", this.clientId) ;
		    	
		        // Ejecutamos la solicitud HTTP
		        closeableHttpResponse = closeableHttpClient.execute(postRequest) ;

		        // Verificamos el estado de la respuesta HTTP
		        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode() ;
		        
		        if (statusCode != 200)
		        {
		            // Si la respuesta no es exitosa, lanza una excepción
		            String errorString = "Error al obtener el token JWT. El código de respuesta es: " + statusCode ;
		            
		            log.error(errorString) ;
		            throw new BaseClientException(BaseClientConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString) ;
		        }

		        // Si el código de respuesta es 200, obtenemos el contenido del cuerpo, que es el token JWT
	            token = EntityUtils.toString(closeableHttpResponse.getEntity()) ;

	            // Almacenar el nuevo token en la "sesión"
	            this.sessionStorageService.setToken(token) ;
		    }
			catch (SocketTimeoutException socketTimeoutException)
			{
				String errorString = "SocketTimeoutException de lectura o escritura al comunicarse con el servidor (token JWT)" ;
				
				log.error(errorString, socketTimeoutException) ;
				throw new BaseClientException(BaseClientConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, socketTimeoutException) ;
	        }
			catch (ConnectTimeoutException connectTimeoutException)
			{
				String errorString = "ConnectTimeoutException al intentar conectar con el servidor (token JWT)" ;
				
				log.error(errorString, connectTimeoutException) ;
				throw new BaseClientException(BaseClientConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, connectTimeoutException) ;
	        }
		    catch (IOException ioException)
		    {
		        String errorString = "IOException mientras se obtenía el token JWT del servidor" ;
		        
		        log.error(errorString, ioException) ;
		        throw new BaseClientException(BaseClientConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, ioException) ;
		    }
		    finally
		    {
		    	if (closeableHttpResponse != null)
		    	{
		    		try
		    		{
						closeableHttpResponse.close() ;
					}
		    		catch (IOException ioException)
		    		{
		    	        String errorString = "IOException mientras se cerraba la respuesta al obtener el token JWT del servidor" ;
		    	        
		    	        log.error(errorString, ioException) ;
		    	        throw new BaseClientException(BaseClientConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, ioException) ;
					}
		    	}
		    	
				try
				{
					// Cerramos el CloseableHttpClient
					closeableHttpClient.close() ;
				}
				catch (IOException ioException)
				{
					String errorString = "Error al cerrar CloseableHttpClient: " + ioException.getMessage() ;
					
					log.error(errorString, ioException) ;
	    	        throw new BaseClientException(BaseClientConstants.ERR_GETTING_PERSONALIZED_TOKEN_JWT, errorString, ioException) ;
				}
		    }
		}
		
		return token ;
	}
	
	/**
	 * Verificar si el token JWT ha expirado
	 *
	 * @param token El token JWT
	 * @return true si el token ha expirado, de lo contrario, false
	 */
	private boolean tokenExpirado(String token)
	{
		try
		{
		    // Parseamos y decodificamos el token JWT utilizando la clave pública y obtenemos los claims
		    Claims claims = this.jwtParser.parseSignedClaims(token) // Parsea el JWT firmado y verifica su firma
		                        		  .getPayload() ; 			// Obtiene el cuerpo (claims) del JWT
	
			// Obtenemos la fecha de expiración del token
			Date expirationDate = claims.getExpiration() ;
	
			// Obtenemos el tiempo actual
			Date now = new Date() ;
	
			// Devuelve true si el token ha expirado, de lo contrario, false
			return expirationDate.before(now) ;
		}
		catch (Exception exception)
		{
			// Si ocurre una excepción (por ejemplo, token inválido), lo consideramos expirado
			return true ;
		}
	}
}
