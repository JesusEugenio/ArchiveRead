package archiveread.ui;

import archiveread.utils.PaletaColores;
import archiveread.modelos.Libro;
import archiveread.utils.CargarFuente;
import archiveread.utils.UIUtils;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class VistaListaLibros extends JPanel {
    
    // Ahora la funcion muestra la lista de libros de acuerdo a la categoria (por defecto "Todas)
    public VistaListaLibros(String tituloLabel, ArrayList<Libro> librosMostrados, String filtroActual,
                            ArrayList<String> listaCategorias,
                            Consumer<String> onFiltrar, Runnable onReporteCat, Runnable onReporteAut,
                            Consumer<Libro> onLibroSeleccionado) {
        
        // Contenedor base
        setLayout(new BorderLayout(20, 20));
        setBackground(PaletaColores.FONDO_PRINCIPAL);
        setBorder(new EmptyBorder(20, 40, 20, 40));
        
        JLabel lblTitulo = new JLabel(tituloLabel);
        lblTitulo.setFont(CargarFuente.get(CargarFuente.BOLD, 22f));
        add(lblTitulo, BorderLayout.NORTH);
        
        // Seccion Central - Aqui se apilan las entradas de cada libro
        JPanel panelListaLibros = new JPanel();
        panelListaLibros.setLayout(new BoxLayout(panelListaLibros, BoxLayout.Y_AXIS));
        panelListaLibros.setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        if (librosMostrados.isEmpty()) {
            panelListaLibros.add(UIUtils.crearEtiquetaVacia("No se encontraron libros para esta vista"));
        } else {
            for (Libro l : librosMostrados) {
                // [EXPLICACIÓN CONSUMER]: A cada tarjeta le decimos "Si te tocan, manda ESTE libro 'l' por el cable al Main".
                panelListaLibros.add(new TarjetaLibro(l, () -> onLibroSeleccionado.accept(l))); 
            }
        }
        
        // Envolvemos la lista en un scrollPane
        JScrollPane scrollPane = new JScrollPane(panelListaLibros);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(PaletaColores.FONDO_PRINCIPAL);
        
        // Forzamos el scroll al inicio (arriba)
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
        add(scrollPane, BorderLayout.CENTER);
        
        // Seccion Derecha - Filtros y Generacion de reportes (aun en desarrollo)
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(PaletaColores.BLANCO);
        sidebar.setBorder(new LineBorder(PaletaColores.BORDE_CLARO, 1, true));
        sidebar.setPreferredSize(new Dimension(300, 320));
        
        JLabel lblSel = new JLabel("Seleccion de Libros");
        lblSel.setFont(CargarFuente.get(CargarFuente.BOLD, 16f));
        lblSel.setBounds(20, 20, 200, 20); // Nos referimos al tamaño y posicion de este texto
        sidebar.add(lblSel);
    
        JLabel lblCat = new JLabel("Categoria");
        lblCat.setFont(CargarFuente.get(CargarFuente.REGULAR, 14f));
        lblCat.setBounds(20, 60, 200, 15);
        sidebar.add(lblCat);
        
        // Cargamos categorias existentes
        if (!listaCategorias.contains("Todas")) {
            listaCategorias.add(0, "Todas");
        }
        
        JComboBox<String> comboCategorias = new JComboBox<>(listaCategorias.toArray(new String[0])); // Convertir en arreglo FIJO para ser usado aqui
        comboCategorias.setFont(CargarFuente.get(CargarFuente.REGULAR, 14f));
        comboCategorias.setSelectedItem(filtroActual);
        comboCategorias.setBounds(20, 80, 250, 30);
        sidebar.add(comboCategorias);
        
        // Boton para aplicar filtro y actualizar la vista
        JButton btnFiltrar = UIUtils.crearBotonEstandar("Mostrar Libros");
        btnFiltrar.setBounds(20, 140, 250, 30);
        btnFiltrar.addActionListener( e -> {
            // Leemos el texto del combo y lo mandamos por el cable 'onFiltrar' al Main
            onFiltrar.accept(comboCategorias.getSelectedItem().toString());
        });
        sidebar.add(btnFiltrar);
        
        // Enlaces para generar reportes en archivos .txt "AUN EN PROCESO / REDISEÑO"
        JLabel lblReportesTitulo = UIUtils.crearLabel("Generar Reportes (.txt)", CargarFuente.BOLD, 15f, PaletaColores.TEXTO_NEGRO);
        lblReportesTitulo.setBounds(20, 210, 250, 20);
        sidebar.add(lblReportesTitulo);

        JLabel lblReporteCat = UIUtils.crearLabel("Reporte de Categorías", CargarFuente.REGULAR, 14f, PaletaColores.PRIMARIO);
        lblReporteCat.setBounds(20, 240, 250, 20);
        lblReporteCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblReporteCat.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onReporteCat.run(); }
        });
        sidebar.add(lblReporteCat);

        JLabel lblReporteAut = UIUtils.crearLabel("Reporte de Autores", CargarFuente.REGULAR, 14f, PaletaColores.PRIMARIO);
        lblReporteAut.setBounds(20, 270, 250, 20);
        lblReporteAut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblReporteAut.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onReporteAut.run(); }
        });
        sidebar.add(lblReporteAut);

        // Envoltorio para fijar el Sidebar arriba y evitar que se estire con la ventana
        JPanel wrapperFiltro = new JPanel(new BorderLayout());
        wrapperFiltro.setBackground(PaletaColores.FONDO_PRINCIPAL);
        wrapperFiltro.add(sidebar, BorderLayout.NORTH);
        add(wrapperFiltro, BorderLayout.EAST);
    }
}