package es.iesjandula.reaktor.base_client.dtos;

import java.util.List;

/**
 * DTO para enviar notificaciones por email
 */
public class NotificationEmailDto 
{
    /* Atributo - Lista de destinatarios */
    private List<String> to;

    /* Atributo - Lista de destinatarios en copia carbono */
    private List<String> cc;

    /* Atributo - Lista de destinatarios en copia carbono oculta */
    private List<String> bcc;

    /* Atributo - Asunto de la notificación */
    private String subject;

    /* Atributo - Cuerpo de la notificación */
    private String body;


    /** Constructor vacío */
    public NotificationEmailDto()
    {
        // Vacío
    }

    /** 
     * Constructor con todos los atributos
     * 
     * @param to Lista de destinatarios
     * @param cc Lista de destinatarios en copia carbono
     * @param bcc Lista de destinatarios en copia carbono oculta
     * @param subject Asunto de la notificación
     * @param body Cuerpo de la notificación
     */
    public NotificationEmailDto(List<String> to, List<String> cc, List<String> bcc, String subject, String body)
    {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
    }

    /**
     * @return Lista de destinatarios
     */
    public List<String> getTo()
    {
        return this.to;
    }

    /**
     * @return Lista de destinatarios en copia carbono
     */
    public List<String> getCc()
    {
        return this.cc;
    }

    /**
     * @return Lista de destinatarios en copia carbono oculta
     */
    public List<String> getBcc()
    {
        return this.bcc;
    }

    /**
     * @return Asunto de la notificación
     */
    public String getSubject()
    {
        return this.subject;
    }

    /**
     * @return Cuerpo de la notificación
     */
    public String getBody()
    {
        return this.body;
    }

    /**
     * @param to Lista de destinatarios
     */
    public void setTo(List<String> to)
    {
        this.to = to;
    }

    /**
     * @param cc Lista de destinatarios en copia carbono
     */
    public void setCc(List<String> cc)
    {
        this.cc = cc;
    }

    /**
     * @param bcc Lista de destinatarios en copia carbono oculta
     */
    public void setBcc(List<String> bcc)
    {
        this.bcc = bcc;
    }
    
    /**
     * @param subject Asunto de la notificación
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    /**
     * @param body Cuerpo de la notificación
     */
    public void setBody(String body)
    {
        this.body = body;
    }
}
