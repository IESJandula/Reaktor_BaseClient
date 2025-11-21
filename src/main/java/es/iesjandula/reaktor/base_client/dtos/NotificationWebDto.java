package es.iesjandula.reaktor.base_client.dtos;

/**
 * DTO para enviar notificaciones web
 */
public class NotificationWebDto
{
    /* Atributo - Texto */
    private String texto;

    /* Atributo - Fecha de inicio */
    private String fechaInicio;

    /* Atributo - Hora de inicio */
    private String horaInicio;

    /* Atributo - Fecha de fin */
    private String fechaFin;

    /* Atributo - Hora de fin */
    private String horaFin;

    /* Atributo - Receptor */
    private String receptor;

    /* Atributo - Tipo */
    private String tipo;

    /**
     * Constructor vacío
     */
    public NotificationWebDto()
    {
        // Vacío
    }
    
    /**
     * Constructor con todos los atributos
     * 
     * @param texto Texto de la notificación
     * @param fechaInicio Fecha de inicio
     * @param horaInicio Hora de inicio
     * @param fechaFin Fecha de fin
     * @param horaFin Hora de fin
     * @param receptor Receptor
     * @param tipo Tipo
     */
    public NotificationWebDto(String texto, String fechaInicio, String horaInicio, String fechaFin, String horaFin, String receptor, String tipo)
    {
        this.texto = texto;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.fechaFin = fechaFin;
        this.horaFin = horaFin;
        this.receptor = receptor;
        this.tipo = tipo;
    }   

    /**
     * @return Texto de la notificación
     */
    public String getTexto()
    {
        return this.texto;
    }
    
    /**
     * @return Fecha de inicio
     */
    public String getFechaInicio()
    {
        return this.fechaInicio;
    }
    
    /**
     * @return Hora de inicio
     */
    public String getHoraInicio()
    {
        return this.horaInicio;
    }
    
    /**
     * @return Fecha de fin
     */
    public String getFechaFin()
    {
        return this.fechaFin;
    }
    
    /**
     * @return Hora de fin
     */
    public String getHoraFin()
    {
        return this.horaFin;
    }

    /**
     * @return Receptor
     */
    public String getReceptor()
    {
        return this.receptor;
    }
    
    /**
     * @return Tipo
     */
    public String getTipo()
    {
        return this.tipo;
    }
    
    /**
     * @param texto Texto de la notificación
     */
    public void setTexto(String texto)
    {
        this.texto = texto;
    }
    
    /**
     * @param fechaInicio Fecha de inicio
     */
    public void setFechaInicio(String fechaInicio)
    {
        this.fechaInicio = fechaInicio;
    }
    
    /**
     * @param horaInicio Hora de inicio
     */
    public void setHoraInicio(String horaInicio)
    {
        this.horaInicio = horaInicio;
    }
    
    /**
     * @param fechaFin Fecha de fin
     */
    public void setFechaFin(String fechaFin)
    {
        this.fechaFin = fechaFin;
    }
    
    /**
     * @param horaFin Hora de fin
     */
    public void setHoraFin(String horaFin)
    {
        this.horaFin = horaFin;
    }
    
    /**
     * @param receptor Receptor
     */
    public void setReceptor(String receptor)
    {
        this.receptor = receptor;
    }

    /**
     * @param tipo Tipo
     */
    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }
}
