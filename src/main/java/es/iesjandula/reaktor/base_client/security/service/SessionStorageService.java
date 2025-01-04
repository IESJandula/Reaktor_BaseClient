package es.iesjandula.reaktor.base_client.security.service;

import org.springframework.stereotype.Component;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Component
public class SessionStorageService
{
	/** Atributo - Token JWT */
	private String tokenJwt ;

	/**
	 * @return token JWT
	 */
	public String getToken()
	{
		return this.tokenJwt ;
	}

	/**
	 * @param tokenJwt token JWT
	 */
	public void setToken(String tokenJwt)
	{
		this.tokenJwt = tokenJwt ;
	}
}
