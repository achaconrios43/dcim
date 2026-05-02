package com.example.dcim.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dcim.dao.MedicionTemperaturaRepository;
import com.example.dcim.dao.PuntoMedicionRepository;
import com.example.dcim.dao.SalaRepository;
import com.example.dcim.dao.SitioRepository;
import com.example.dcim.entity.GestionAcceso;
import com.example.dcim.entity.IngresoAP;
import com.example.dcim.entity.Inventario;
import com.example.dcim.entity.MedicionTemperatura;
import com.example.dcim.entity.PuntoMedicion;
import com.example.dcim.entity.Sala;
import com.example.dcim.entity.Sitio;

@Service
@Transactional
public class TemperaturaService {

    @Autowired
    private SitioRepository sitioRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private PuntoMedicionRepository puntoMedicionRepository;

    @Autowired
    private MedicionTemperaturaRepository medicionTemperaturaRepository;

    public Sitio crearSitio(String nombre, String descripcion) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre del sitio es obligatorio");
        }
        Optional<Sitio> existente = sitioRepository.findByNombreIgnoreCase(nombre.trim());
        if (existente.isPresent()) {
            throw new Exception("Ya existe un sitio con ese nombre");
        }
        Sitio sitio = new Sitio();
        sitio.setNombre(nombre.trim());
        sitio.setDescripcion(descripcion != null ? descripcion.trim() : null);
        return sitioRepository.save(sitio);
    }

    public Sala crearSala(Long sitioId, String nombre, String descripcion) throws Exception {
        if (sitioId == null) {
            throw new Exception("Debe seleccionar un sitio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre de la sala es obligatorio");
        }
        Sitio sitio = sitioRepository.findById(sitioId)
                .orElseThrow(() -> new Exception("Sitio no encontrado"));

        Optional<Sala> existente = salaRepository.findBySitioIdAndNombreIgnoreCase(sitioId, nombre.trim());
        if (existente.isPresent()) {
            throw new Exception("Ya existe una sala con ese nombre en el sitio seleccionado");
        }

        Sala sala = new Sala();
        sala.setSitio(sitio);
        sala.setNombre(nombre.trim());
        sala.setDescripcion(descripcion != null ? descripcion.trim() : null);
        return salaRepository.save(sala);
    }

    public PuntoMedicion crearPunto(Long salaId, String codigo, String nombre, BigDecimal min, BigDecimal max) throws Exception {
        if (salaId == null) {
            throw new Exception("Debe seleccionar una sala");
        }
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new Exception("El codigo del punto es obligatorio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre del punto es obligatorio");
        }

        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new Exception("Sala no encontrada"));

        PuntoMedicion punto = new PuntoMedicion();
        punto.setSala(sala);
        punto.setCodigo(codigo.trim().toUpperCase());
        punto.setNombre(nombre.trim());
        punto.setTemperaturaMinima(min != null ? min : new BigDecimal("18.00"));
        punto.setTemperaturaMaxima(max != null ? max : new BigDecimal("27.00"));

        if (punto.getTemperaturaMinima().compareTo(punto.getTemperaturaMaxima()) >= 0) {
            throw new Exception("La temperatura minima debe ser menor que la maxima");
        }

        return puntoMedicionRepository.save(punto);
    }

    public MedicionTemperatura registrarMedicion(Long puntoId, LocalDate fecha, BigDecimal temperatura) throws Exception {
        if (puntoId == null) {
            throw new Exception("Debe seleccionar un punto");
        }
        if (temperatura == null) {
            throw new Exception("Debe ingresar la temperatura");
        }

        PuntoMedicion punto = puntoMedicionRepository.findById(puntoId)
                .orElseThrow(() -> new Exception("Punto no encontrado"));

        BigDecimal tMin = punto.getTemperaturaMinima();
        BigDecimal tMax = punto.getTemperaturaMaxima();
        if (tMin != null && temperatura.compareTo(tMin) < 0) {
            throw new Exception("Temperatura " + temperatura + " °C está por debajo del mínimo permitido ("
                    + tMin + " °C) para el punto '" + punto.getNombre() + "'");
        }
        if (tMax != null && temperatura.compareTo(tMax) > 0) {
            throw new Exception("Temperatura " + temperatura + " °C supera el máximo permitido ("
                    + tMax + " °C) para el punto '" + punto.getNombre() + "'");
        }

        MedicionTemperatura medicion = new MedicionTemperatura();
        medicion.setPunto(punto);
        medicion.setFechaMedicion(fecha != null ? fecha : LocalDate.now());
        medicion.setTemperaturaCelsius(temperatura.setScale(2, RoundingMode.HALF_UP));
        medicion.setEstado(calcularEstado(temperatura, tMin, tMax));

        return medicionTemperaturaRepository.save(medicion);
    }

    public void eliminarMedicion(Long medicionId) throws Exception {
        if (medicionId == null) {
            throw new Exception("ID de medicion invalido");
        }
        MedicionTemperatura medicion = medicionTemperaturaRepository.findById(medicionId)
                .orElseThrow(() -> new Exception("Medicion no encontrada"));
        medicionTemperaturaRepository.delete(medicion);
    }

    public List<Sitio> listarSitiosActivos() {
        return sitioRepository.findByActivoTrueOrderByNombreAsc();
    }

    public List<Sala> listarSalasPorSitio(Long sitioId) {
        if (sitioId == null) {
            return List.of();
        }
        return salaRepository.findBySitioIdOrderByNombreAsc(sitioId);
    }

    public List<PuntoMedicion> listarPuntosPorSala(Long salaId) {
        if (salaId == null) {
            return List.of();
        }
        return puntoMedicionRepository.findBySalaIdAndActivoTrueOrderByNombreAsc(salaId);
    }

        /**
         * Retorna los puntos de medicion activos de la sala que AUN NO tienen
         * medicion registrada en la fecha indicada (por defecto hoy).
         */
        public List<PuntoMedicion> listarPuntosPendientesPorSala(Long salaId, LocalDate fecha) {
            if (salaId == null) {
                return List.of();
            }
            LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
            List<PuntoMedicion> todos = puntoMedicionRepository.findBySalaIdAndActivoTrueOrderByNombreAsc(salaId);
            List<Long> yaRegistrados = medicionTemperaturaRepository.findPuntoIdsRegistradosEnFecha(salaId, fechaConsulta);
            return todos.stream()
                    .filter(p -> !yaRegistrados.contains(p.getId()))
                    .collect(Collectors.toList());
        }

        public long contarPuntosTotalesPorSala(Long salaId) {
            if (salaId == null) return 0;
            return puntoMedicionRepository.countBySalaId(salaId);
        }

        public long contarPuntosRegistradosHoy(Long salaId, LocalDate fecha) {
            if (salaId == null) return 0;
            LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
            return medicionTemperaturaRepository.findPuntoIdsRegistradosEnFecha(salaId, fechaConsulta).size();
        }

    public RegistroLoteResultado registrarMedicionesPorSala(Long salaId, LocalDate fecha, Map<Long, BigDecimal> temperaturasPorPunto)
            throws Exception {
        if (salaId == null) {
            throw new Exception("Debe seleccionar una sala");
        }

        List<PuntoMedicion> puntosSala = listarPuntosPorSala(salaId);
        if (puntosSala.isEmpty()) {
            throw new Exception("La sala seleccionada no tiene puntos de medicion activos");
        }

        Map<Long, PuntoMedicion> puntosPorId = puntosSala.stream()
                .collect(Collectors.toMap(PuntoMedicion::getId, p -> p, (a, b) -> a, LinkedHashMap::new));

        int totalGuardadas = 0;
        BigDecimal suma = BigDecimal.ZERO;
        LocalDate fechaRegistro = fecha != null ? fecha : LocalDate.now();

        for (Map.Entry<Long, BigDecimal> entry : temperaturasPorPunto.entrySet()) {
            Long puntoId = entry.getKey();
            BigDecimal temperatura = entry.getValue();

            if (puntoId == null || temperatura == null) {
                continue;
            }

            PuntoMedicion punto = puntosPorId.get(puntoId);
            if (punto == null) {
                throw new Exception("Hay puntos que no pertenecen a la sala seleccionada");
            }

            registrarMedicion(punto.getId(), fechaRegistro, temperatura);
            totalGuardadas++;
            suma = suma.add(temperatura);
            }

            // Verificar que todos los puntos pendientes fueron ingresados
            List<PuntoMedicion> pendientesAhora = listarPuntosPendientesPorSala(salaId, fechaRegistro);
            if (!pendientesAhora.isEmpty()) {
                StringBuilder faltantes = new StringBuilder();
                for (PuntoMedicion p : pendientesAhora) {
                    faltantes.append(p.getNombre()).append(", ");
                }
                throw new Exception("Faltan registrar temperaturas para: "
                        + faltantes.toString().replaceAll(", $", "")
                        + ". Debe completar TODOS los puntos de la sala.");
            }

        if (totalGuardadas == 0) {
            throw new Exception("Debe ingresar al menos una temperatura para guardar");
        }

        BigDecimal promedio = suma.divide(BigDecimal.valueOf(totalGuardadas), 2, RoundingMode.HALF_UP);
        return new RegistroLoteResultado(totalGuardadas, promedio, fechaRegistro);
    }

    public BigDecimal obtenerPromedioDiarioSala(Long salaId, LocalDate fecha) {
        if (salaId == null) {
            return BigDecimal.ZERO;
        }
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        Double promedio = medicionTemperaturaRepository.promedioDiarioPorSala(salaId, fechaConsulta);
        return promedio != null ? BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public List<MedicionTemperatura> ultimasMedicionesSala(Long salaId, LocalDate fecha) {
        if (salaId == null) {
            return List.of();
        }
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        return medicionTemperaturaRepository.findTop20ByPuntoSalaIdAndFechaMedicionOrderByFechaRegistroDesc(salaId, fechaConsulta);
    }

    public List<ResumenSalaDiario> obtenerResumenDiarioPorSitioId(Long sitioId, LocalDate fecha) {
        if (sitioId == null) {
            return List.of();
        }

        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        List<Sala> salas = listarSalasPorSitio(sitioId);

        return salas.stream().map(sala -> {
            int totalPuntos = listarPuntosPorSala(sala.getId()).size();
            BigDecimal promedio = obtenerPromedioDiarioSala(sala.getId(), fechaConsulta);
            Long puntosMedidos = medicionTemperaturaRepository.puntosMedidosDiariosPorSala(sala.getId(), fechaConsulta);
            Long alertas = medicionTemperaturaRepository.alertasDiariasPorSala(sala.getId(), fechaConsulta);

            return new ResumenSalaDiario(
                    sala.getId(),
                    sala.getNombre(),
                    totalPuntos,
                    puntosMedidos != null ? puntosMedidos.intValue() : 0,
                    promedio,
                    alertas != null ? alertas.intValue() : 0);
        }).toList();
    }

    public List<Sala> listarSalasPorNombreSitio(String sitio) {
        if (sitio == null || sitio.trim().isEmpty()) {
            return List.of();
        }
        return salaRepository.findBySitioNombreIgnoreCaseOrderByNombreAsc(sitio.trim());
    }

    public List<Sala> listarTodasLasSalas() {
        return salaRepository.findAll().stream()
                .sorted(Comparator.comparing(Sala::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Map<String, List<String>> obtenerMapaSalasPorSitio() {
        return salaRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        s -> s.getSitio().getNombre(),
                        Collectors.mapping(Sala::getNombre, Collectors.toList())));
    }

    public TemperaturaResumen obtenerResumenDiario(String sitio, LocalDate fecha) {
        if (sitio == null || sitio.trim().isEmpty()) {
            return TemperaturaResumen.vacio(fecha);
        }

        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        Double promedio = medicionTemperaturaRepository.promedioDiarioPorSitio(sitio, fechaConsulta);
        Long alertas = medicionTemperaturaRepository.alertasDiariasPorSitio(sitio, fechaConsulta);

        List<PuntoMedicion> puntos = puntoMedicionRepository.findBySalaSitioNombreIgnoreCaseOrderByNombreAsc(sitio);
        long puntosConAlerta = puntos.stream()
                .map(p -> medicionTemperaturaRepository.findTopByPuntoIdOrderByFechaRegistroDesc(p.getId()).orElse(null))
                .filter(m -> m != null && !"OK".equalsIgnoreCase(m.getEstado()))
                .count();

        return new TemperaturaResumen(
                promedio != null ? BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                fechaConsulta,
                puntos.size(),
                Math.toIntExact(puntosConAlerta),
                alertas != null ? alertas.intValue() : 0,
                obtenerEstadoGeneral((int) puntosConAlerta));
    }

    public TemperaturaResumen obtenerResumenMensual(String sitio, LocalDate fechaReferencia) {
        if (sitio == null || sitio.trim().isEmpty()) {
            return TemperaturaResumen.vacio(fechaReferencia);
        }

        LocalDate base = fechaReferencia != null ? fechaReferencia : LocalDate.now();
        LocalDate inicio = base.withDayOfMonth(1);
        LocalDate fin = base.withDayOfMonth(base.lengthOfMonth());

        Double promedio = medicionTemperaturaRepository.promedioMensualPorSitio(sitio, inicio, fin);
        Long alertas = medicionTemperaturaRepository.alertasMensualesPorSitio(sitio, inicio, fin);
        long totalPuntos = puntoMedicionRepository.countBySalaSitioNombreIgnoreCaseAndActivoTrue(sitio);

        return new TemperaturaResumen(
                promedio != null ? BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                base,
                (int) totalPuntos,
                0,
                alertas != null ? alertas.intValue() : 0,
                alertas != null && alertas > 0 ? "ALERTA" : "OK");
    }

    public List<MedicionTemperatura> ultimasMedicionesDiarias(String sitio, LocalDate fecha) {
        if (sitio == null || sitio.trim().isEmpty()) {
            return List.of();
        }
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        List<MedicionTemperatura> mediciones = medicionTemperaturaRepository
                .findByPuntoSalaSitioNombreIgnoreCaseAndFechaMedicionOrderByFechaRegistroDesc(sitio, fechaConsulta);

        return mediciones.stream()
                .sorted(Comparator.comparing(MedicionTemperatura::getFechaRegistro).reversed())
                .limit(15)
                .toList();
    }

    public void vincularIngresoAPConUbicacion(IngresoAP ingresoAP) {
        if (ingresoAP == null) {
            return;
        }
        Sitio sitio = resolverSitioPorNombre(ingresoAP.getSitioIngreso());
        ingresoAP.setSitioRef(sitio);

        if (sitio != null) {
            Sala sala = resolverSalaPorNombre(sitio.getId(), extraerPrimeraSala(ingresoAP.getSalaIngresa()));
            ingresoAP.setSalaRef(sala);
        } else {
            ingresoAP.setSalaRef(null);
        }
    }

    public void vincularGestionConUbicacion(GestionAcceso gestion) {
        if (gestion == null) {
            return;
        }
        Sitio sitio = resolverSitioPorNombre(gestion.getSitio());
        gestion.setSitioRef(sitio);
        gestion.setSalaRef(null);
    }

    public void vincularInventarioConUbicacion(Inventario inventario) {
        if (inventario == null) {
            return;
        }
        Sitio sitio = resolverSitioPorNombre(inventario.getSitio());
        inventario.setSitioRef(sitio);
        if (sitio != null) {
            inventario.setSalaRef(resolverSalaPorNombre(sitio.getId(), inventario.getSala()));
        } else {
            inventario.setSalaRef(null);
        }
    }

    private Sitio resolverSitioPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        return sitioRepository.findByNombreIgnoreCase(nombre.trim()).orElse(null);
    }

    private Sala resolverSalaPorNombre(Long sitioId, String nombreSala) {
        if (sitioId == null || nombreSala == null || nombreSala.trim().isEmpty()) {
            return null;
        }
        return salaRepository.findBySitioIdAndNombreIgnoreCase(sitioId, nombreSala.trim()).orElse(null);
    }

    private String extraerPrimeraSala(String salaIngresa) {
        if (salaIngresa == null || salaIngresa.trim().isEmpty()) {
            return null;
        }
        String[] partes = salaIngresa.split(",");
        return partes.length > 0 ? partes[0].trim() : salaIngresa.trim();
    }

    private String calcularEstado(BigDecimal temperatura, BigDecimal min, BigDecimal max) {
        if (temperatura.compareTo(min) < 0) {
            return "ALERTA_BAJA";
        }
        if (temperatura.compareTo(max) > 0) {
            return "ALERTA_ALTA";
        }
        return "OK";
    }

    private String obtenerEstadoGeneral(int puntosConAlerta) {
        return puntosConAlerta > 0 ? "ALERTA" : "OK";
    }

    public static class TemperaturaResumen {
        private final BigDecimal promedio;
        private final LocalDate fecha;
        private final int totalPuntos;
        private final int puntosEnAlerta;
        private final int totalAlertasPeriodo;
        private final String estadoGeneral;

        public TemperaturaResumen(BigDecimal promedio, LocalDate fecha, int totalPuntos, int puntosEnAlerta,
                int totalAlertasPeriodo, String estadoGeneral) {
            this.promedio = promedio;
            this.fecha = fecha;
            this.totalPuntos = totalPuntos;
            this.puntosEnAlerta = puntosEnAlerta;
            this.totalAlertasPeriodo = totalAlertasPeriodo;
            this.estadoGeneral = estadoGeneral;
        }

        public static TemperaturaResumen vacio(LocalDate fecha) {
            return new TemperaturaResumen(BigDecimal.ZERO, fecha != null ? fecha : LocalDate.now(), 0, 0, 0, "SIN_DATOS");
        }

        public BigDecimal getPromedio() {
            return promedio;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public int getTotalPuntos() {
            return totalPuntos;
        }

        public int getPuntosEnAlerta() {
            return puntosEnAlerta;
        }

        public int getTotalAlertasPeriodo() {
            return totalAlertasPeriodo;
        }

        public String getEstadoGeneral() {
            return estadoGeneral;
        }
    }

    public static class RegistroLoteResultado {
        private final int totalGuardadas;
        private final BigDecimal promedioSala;
        private final LocalDate fecha;

        public RegistroLoteResultado(int totalGuardadas, BigDecimal promedioSala, LocalDate fecha) {
            this.totalGuardadas = totalGuardadas;
            this.promedioSala = promedioSala;
            this.fecha = fecha;
        }

        public int getTotalGuardadas() {
            return totalGuardadas;
        }

        public BigDecimal getPromedioSala() {
            return promedioSala;
        }

        public LocalDate getFecha() {
            return fecha;
        }
    }

    public static class ResumenSalaDiario {
        private final Long salaId;
        private final String salaNombre;
        private final int totalPuntos;
        private final int puntosMedidos;
        private final BigDecimal promedio;
        private final int alertas;

        public ResumenSalaDiario(Long salaId, String salaNombre, int totalPuntos, int puntosMedidos, BigDecimal promedio,
                int alertas) {
            this.salaId = salaId;
            this.salaNombre = salaNombre;
            this.totalPuntos = totalPuntos;
            this.puntosMedidos = puntosMedidos;
            this.promedio = promedio;
            this.alertas = alertas;
        }

        public Long getSalaId() {
            return salaId;
        }

        public String getSalaNombre() {
            return salaNombre;
        }

        public int getTotalPuntos() {
            return totalPuntos;
        }

        public int getPuntosMedidos() {
            return puntosMedidos;
        }

        public BigDecimal getPromedio() {
            return promedio;
        }

        public int getAlertas() {
            return alertas;
        }
    }
}
