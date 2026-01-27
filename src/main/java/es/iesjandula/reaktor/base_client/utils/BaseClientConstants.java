package es.iesjandula.reaktor.base_client.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class BaseClientConstants
{
	/*********************************************************/
	/*********************** Errores *************************/
	/*********************************************************/
	
	/** Error - Excepción genérica - Código */
	public static final int ERR_GENERIC_EXCEPTION_CODE 	 		     = 0 ;
	
	/** Error - Excepción genérica - Mensaje */
	public static final String ERR_GENERIC_EXCEPTION_MSG 		     = "Excepción genérica en " ;
	
	/** Error - Error mientras se obtenía el token personalizado JWT */
	public static final int ERR_GETTING_PERSONALIZED_TOKEN_JWT       = 1 ;

	/** Error - Error al enviar el email */
	public static final int ERR_SENDING_EMAIL 				         = 2 ;

	/** Error - Error al enviar la notificación web */
	public static final int ERR_INVALID_PARAMETER_NOTIFICATION_WEB   = 3 ;

	/** Error - Error al enviar la notificación email */
	public static final int ERR_INVALID_PARAMETER_NOTIFICATION_EMAIL = 4 ;

	/** Error - Error al obtener los usuarios de Firebase */
	public static final int ERR_GETTING_USERS 				         = 5 ;

	/** Error - Error al imprimir el PDF */
	public static final int ERR_PRINTING_PDF 				         = 6 ;

	/*********************************************************/
	/****** Parámetros para envío de notificaciones **********/
	/*********************************************************/

	/** Parámetro - Texto de la notificación */
	public static final String PARAM_TEXTO_NOTIFICACION 			= "texto" ;

	/** Parámetro - Fecha de inicio de la notificación */
	public static final String PARAM_FECHA_INICIO_NOTIFICACION 	    = "fechaInicio" ;

	/** Parámetro - Hora de inicio de la notificación */
	public static final String PARAM_HORA_INICIO_NOTIFICACION 	    = "horaInicio" ;

	/** Parámetro - Fecha de fin de la notificación */
	public static final String PARAM_FECHA_FIN_NOTIFICACION 	    = "fechaFin" ;

	/** Parámetro - Hora de fin de la notificación */
	public static final String PARAM_HORA_FIN_NOTIFICACION 	        = "horaFin" ;

	/** Parámetro - Receptor de la notificación */
	public static final String PARAM_RECEPTOR_NOTIFICACION 	        = "receptor" ;

	/** Parámetro - Tipo de notificación */
	public static final String PARAM_TIPO_NOTIFICACION 			    = "tipo" ;

	/*********************************************************/
	/*************** Tipos de notificaciones *****************/
	/*********************************************************/
	
	/** Tipo de notificación - Solo texto */
	public static final String TIPO_NOTIFICACION_SOLO_TEXTO     = "Solo texto" ;
	
	/** Tipo de notificación - Texto e imagen */
	public static final String TIPO_NOTIFICACION_TEXTO_E_IMAGEN = "Texto e imagen" ;

	/** Lista de tipos de notificación */
	public static final List<String> TIPOS_NOTIFICACIONES = Arrays.asList(TIPO_NOTIFICACION_SOLO_TEXTO) ; //, TIPO_NOTIFICACION_TEXTO_E_IMAGEN) ;


	/*********************************************************/
	/*************** Receptores de notificaciones ************/
	/*********************************************************/
	
	/** Receptor de notificación - Profesor */
	public static final String RECEPTOR_NOTIFICACION_CLAUSTRO = "Todo el claustro" ;

	/** Receptor de notificación - Equipo directivo */
	public static final String RECEPTOR_NOTIFICACION_EQUIPO_DIRECTIVO = "Solo el equipo directivo" ;

	/** Receptor de notificación - Administradores */
	public static final String RECEPTOR_NOTIFICACION_ADMINISTRADORES = "Solo administradores" ;
}
