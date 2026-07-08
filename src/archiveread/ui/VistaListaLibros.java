package archiveread.ui;

import archiveread.utils.*;
import archiveread.modelos.Libro;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

// =============================================
// VistaListaLibros
// Pantalla que dibuja el catalogo completo de libros y las opciones de filtrado
// =============================================

public class VistaListaLibros extends JPanel {
	
	// Variables para el control de paginación
    private ArrayList<Libro> todosLosLibros;
    private int paginaActual = 0;
    private final int LIBROS_POR_PAGINA = 5;
    
    // Componentes que necesitan actualización dinámica
    private JPanel panelListaLibros;
    private JPanel panelPaginacion;
    private JLabel lblInfoPagina;
    private JButton btnAnterior;
    private JButton btnSiguiente;
    
	// Recibe el título, la lista de libros ya filtrada y los comandos a ejecutar (Consumers/Runnables)
    public VistaListaLibros(String tituloLabel, ArrayList<Libro> librosMostrados, String filtroActual,
                            ArrayList<String> listaCategorias, Consumer<String> onFiltrar, Runnable onReporteCat,
                            Runnable onReporteAut, Consumer<Libro> onLibroSeleccionado) {
    	
    	this.todosLosLibros = librosMostrados;
        
        // Contenedor base
        setLayout(new BorderLayout(20, 20));
        setBackground(PaletaColores.FONDO_PRINCIPAL);
        setBorder(new EmptyBorder(20, 40, 20, 40));
        
        // Título dinámico (Ej. "Libros Recientes" o "Libros de Programación:")
        JLabel lblTitulo = new JLabel(tituloLabel);
        lblTitulo.setFont(CargarFuente.getBold(22f));
        add(lblTitulo, BorderLayout.NORTH);
        
        // Seccion Central - Aqui se apilan las entradas de cada libro
        panelListaLibros = new JPanel();
        panelListaLibros.setLayout(new BoxLayout(panelListaLibros, BoxLayout.Y_AXIS));
        panelListaLibros.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        // Envolvemos la lista en un scrollPane
        JScrollPane scrollPane = new JScrollPane(panelListaLibros);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(PaletaColores.FONDO_PRINCIPAL);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new ScrollModernoUI()); 
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(14, 0));
        
        // Panel de Controles de Paginación 
        panelPaginacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelPaginacion.setBackground(PaletaColores.FONDO_PRINCIPAL);
        panelPaginacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panelPaginacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnAnterior = UIUtils.crearBotonEstandar("Anterior");
        btnAnterior.setPreferredSize(new Dimension(120, 35));
        
        lblInfoPagina = UIUtils.crearLabel("Página 1 de 1", CargarFuente.BOLD, 14f, PaletaColores.TEXTO_NEGRO);
        
        btnSiguiente = UIUtils.crearBotonEstandar("Siguiente");
        btnSiguiente.setPreferredSize(new Dimension(120, 35));
        
        panelPaginacion.add(btnAnterior);
        panelPaginacion.add(lblInfoPagina);
        panelPaginacion.add(btnSiguiente);
        
        // Listeners para la navegación de páginas
        btnAnterior.addActionListener(e -> {
            if (paginaActual > 0) {
                paginaActual--;
                redibujarLibros(onLibroSeleccionado);
                // Resetea el scroll hacia arriba automáticamente al cambiar de página
                SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
            }
        });
        
        btnSiguiente.addActionListener(e -> {
            int totalPaginas = (int) Math.ceil((double) todosLosLibros.size() / LIBROS_POR_PAGINA);
            if (paginaActual < totalPaginas - 1) {
                paginaActual++;
                redibujarLibros(onLibroSeleccionado);
                // Resetea el scroll hacia arriba automáticamente al cambiar de página
                SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
            }
        });
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Dibujamos la primera página de libros al arrancar la vista
        redibujarLibros(onLibroSeleccionado);
        
        // Forzamos el scroll al inicio (arriba)
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
        
        // Seccion Derecha - Filtros y Generacion de reportes (separados9
        JPanel pnlLateral = new JPanel(null);
        pnlLateral.setLayout(new BoxLayout(pnlLateral, BoxLayout.Y_AXIS));
        pnlLateral.setBackground(PaletaColores.FONDO_PRINCIPAL);
        pnlLateral.setPreferredSize(new Dimension(300, 400));
        
        // Caja de Filtros
        JPanel cajaFiltros = new JPanel(null); // Para usar coordenadas setBounds
        cajaFiltros.setBackground(PaletaColores.BLANCO);
        cajaFiltros.setBorder(new LineBorder(PaletaColores.BORDE_CLARO, 1, true));
        cajaFiltros.setPreferredSize(new Dimension(300, 200));
        cajaFiltros.setMinimumSize(new Dimension(300, 200));
        cajaFiltros.setMaximumSize(new Dimension(300, 200)); // Evita que se estire
        
        JLabel lblSel = new JLabel("Seleccion de Libros");
        lblSel.setFont(CargarFuente.getBold(16f));
        lblSel.setBounds(20, 20, 200, 20); // Nos referimos al tamaño y posicion de este texto
        cajaFiltros.add(lblSel);
    
        JLabel lblCat = new JLabel("Categoria");
        lblCat.setFont(CargarFuente.getRegular(14f));
        lblCat.setBounds(20, 60, 200, 15);
        cajaFiltros.add(lblCat);
        
        // Cargamos categorias existentes
        if (!listaCategorias.contains("Todas")) {
            listaCategorias.add(0, "Todas");
        }
        
        JComboBox<String> comboCategorias = new JComboBox<>(listaCategorias.toArray(new String[0])); // Convertir en arreglo FIJO para ser usado aqui
        UIUtils.aplicarEstiloCombo(comboCategorias);
        comboCategorias.setFont(CargarFuente.getRegular(14f));
        comboCategorias.setSelectedItem(filtroActual);
        comboCategorias.setBounds(20, 80, 250, 30);
        cajaFiltros.add(comboCategorias);
        
        // Boton para aplicar filtro y actualizar la vista
        JButton btnFiltrar = UIUtils.crearBotonEstandar("Mostrar Libros");
        btnFiltrar.setBounds(20, 130, 250, 40);
        btnFiltrar.addActionListener( e -> {
        	// Dispara el cable para decirle al Main que recargue la vista aplicando la categoría seleccionada
            onFiltrar.accept(comboCategorias.getSelectedItem().toString());
        });
        cajaFiltros.add(btnFiltrar);
        
        // Caja de Reportes
        JPanel cajaReportes = new JPanel(null);
        cajaReportes.setBackground(PaletaColores.BLANCO);
        cajaReportes.setBorder(new LineBorder(PaletaColores.BORDE_CLARO, 1, true));
        cajaReportes.setPreferredSize(new Dimension(300, 120));
        cajaReportes.setMinimumSize(new Dimension(300, 200));
        cajaReportes.setMaximumSize(new Dimension(300, 120));
        
        // Enlaces para generar reportes en archivos .txt 
        JLabel lblReportesTitulo = UIUtils.crearLabel("Generar Reportes (.txt)", CargarFuente.BOLD, 15f, PaletaColores.TEXTO_NEGRO);
        lblReportesTitulo.setBounds(20, 20, 250, 20);
        cajaReportes.add(lblReportesTitulo);

        JLabel lblReporteCat = UIUtils.crearLabel("Reporte de Categorías", CargarFuente.REGULAR, 14f, PaletaColores.PRIMARIO);
        lblReporteCat.setBounds(20, 50, 250, 20);
        lblReporteCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblReporteCat.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onReporteCat.run(); }
        });
        cajaReportes.add(lblReporteCat);

        JLabel lblReporteAut = UIUtils.crearLabel("Reporte de Autores", CargarFuente.REGULAR, 14f, PaletaColores.PRIMARIO);
        lblReporteAut.setBounds(20, 80, 250, 20);
        lblReporteAut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblReporteAut.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onReporteAut.run(); }
        });
        cajaReportes.add(lblReporteAut);
        
        // Ensamblamos las cajas en el pnlLateral
        pnlLateral.add(cajaFiltros);
        pnlLateral.add(Box.createVerticalStrut(20)); // Separacion entre cajas
        pnlLateral.add(cajaReportes);

        // Envoltorio para fijar el Sidebar arriba y evitar que se estire con la ventana
        JPanel wrapperFiltro = new JPanel(new BorderLayout());
        wrapperFiltro.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        wrapperFiltro.setPreferredSize(new Dimension(300, 0));
        wrapperFiltro.setMinimumSize(new Dimension(300, 0));
        
        wrapperFiltro.add(pnlLateral, BorderLayout.NORTH);
        add(wrapperFiltro, BorderLayout.EAST);
    }
    
    
    // --- Método enfocado en renderizar únicamente el subconjunto de la página actual ---
    private void redibujarLibros(Consumer<Libro> onLibroSeleccionado) {
        panelListaLibros.removeAll();
        
        if (todosLosLibros.isEmpty()) {
            panelListaLibros.add(UIUtils.crearEtiquetaVacia("No se encontraron libros para esta vista"));
            lblInfoPagina.setText("Página 0 de 0");
            btnAnterior.setEnabled(false);
            btnAnterior.setBackground(PaletaColores.BOTON_DESHABILITADO);
            btnAnterior.setForeground(PaletaColores.TEXTO_GRIS_CLARO);
            btnSiguiente.setEnabled(false);
            btnSiguiente.setBackground(PaletaColores.BOTON_DESHABILITADO);
            btnSiguiente.setForeground(PaletaColores.TEXTO_GRIS_CLARO);
        } else {
            // Cálculos lógicos para la sublista
            int inicio = paginaActual * LIBROS_POR_PAGINA;
            int fin = Math.min(inicio + LIBROS_POR_PAGINA, todosLosLibros.size());
            
            for (int i = inicio; i < fin; i++) {
                Libro l = todosLosLibros.get(i);
                panelListaLibros.add(new TarjetaLibro(l, () -> onLibroSeleccionado.accept(l)));
            }
            
            int totalPaginas = (int) Math.ceil((double) todosLosLibros.size() / LIBROS_POR_PAGINA);
            lblInfoPagina.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);
            
            // Control visual dinámico para el botón Anterior
            if (paginaActual > 0) {
                btnAnterior.setEnabled(true);
                btnAnterior.setBackground(PaletaColores.PRIMARIO);
                btnAnterior.setForeground(PaletaColores.TEXTO_BLANCO);
            } else {
                btnAnterior.setEnabled(false);
                btnAnterior.setBackground(PaletaColores.BOTON_DESHABILITADO);
                btnAnterior.setForeground(PaletaColores.TEXTO_GRIS_CLARO);
            }
            
            // Control visual dinámico para el botón Siguiente
            if (fin < todosLosLibros.size()) {
                btnSiguiente.setEnabled(true);
                btnSiguiente.setBackground(PaletaColores.PRIMARIO);
                btnSiguiente.setForeground(PaletaColores.TEXTO_BLANCO);
            } else {
                btnSiguiente.setEnabled(false);
                btnSiguiente.setBackground(PaletaColores.BOTON_DESHABILITADO);
                btnSiguiente.setForeground(PaletaColores.TEXTO_GRIS_CLARO);
            }
        }
        
        panelListaLibros.add(Box.createVerticalStrut(20));
        panelListaLibros.add(panelPaginacion);
        
        panelListaLibros.revalidate();
        panelListaLibros.repaint();
    }
}
