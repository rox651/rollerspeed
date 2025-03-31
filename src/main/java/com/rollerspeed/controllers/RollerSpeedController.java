package com.rollerspeed.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Información General", description = "API para acceder a la información general de la escuela")
public class RollerSpeedController {

    @GetMapping("/mision")
    @Operation(summary = "Ver misión", description = "Muestra la misión de la escuela de patinaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de misión mostrada exitosamente")
    })
    public String mision() {
        return "mision";
    }

    @GetMapping("/vision")
    @Operation(summary = "Ver visión", description = "Muestra la visión de la escuela de patinaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de visión mostrada exitosamente")
    })
    public String vision() {
        return "vision";
    }

    @GetMapping("/valores")
    @Operation(summary = "Ver valores", description = "Muestra los valores de la escuela de patinaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de valores mostrada exitosamente")
    })
    public String valores() {
        return "valores";
    }

    @GetMapping("/servicios")
    @Operation(summary = "Ver servicios", description = "Muestra los servicios ofrecidos por la escuela")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de servicios mostrada exitosamente")
    })
    public String servicios() {
        return "servicios";
    }

    @GetMapping("/eventos")
    @Operation(summary = "Ver eventos", description = "Muestra los eventos programados por la escuela")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de eventos mostrada exitosamente")
    })
    public String eventos() {
        return "eventos";
    }
}