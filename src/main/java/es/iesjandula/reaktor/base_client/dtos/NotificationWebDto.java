package es.iesjandula.reaktor.base_client.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
