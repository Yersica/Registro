/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hospedajequeen;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistroCliente extends JFrame {

    // Componentes de la interfaz gráfica
    private final JTextField txtNombre;
    private final JTextField txtCedula;
    private final JTextField txtTelefono;
    private final JTextField txtBuscar;
    private final JButton btnAgregar;
    private final JButton btnActualizar;
    private final JButton btnEliminar;
    private final JButton btnBuscar;
    private final JButton btnListar;
    private final JTable tablaClientes;
    private final DefaultTableModel modeloTabla;

    public RegistroCliente() {
        // Configuración de la ventana
        setTitle("Registro de Cliente");
        setSize(600, 450); // Aumenté el tamaño para que quepan todos los botones
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Crear etiquetas
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblCedula = new JLabel("Cédula:");
        JLabel lblTelefono = new JLabel("Teléfono:");
        JLabel lblBuscar = new JLabel("Buscar (Cédula):");

        // Crear campos de texto
        txtNombre = new JTextField();
        txtCedula = new JTextField();
        txtTelefono = new JTextField();
        txtBuscar = new JTextField();

        // Crear el botónes
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar");
        btnListar = new JButton("Listar");
        
        // Crear tabla
        modeloTabla = new DefaultTableModel(new String[]{"Nombre", "Cédula", "Teléfono"}, 0);
        tablaClientes = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaClientes);

        // Posicionar los elementos en la ventana
        lblNombre.setBounds(20, 20, 80, 25);
        txtNombre.setBounds(100, 20, 160, 25);
        
        lblCedula.setBounds(20, 60, 80, 25);
        txtCedula.setBounds(100, 60, 160, 25);
        
        lblTelefono.setBounds(20, 100, 80, 25);
        txtTelefono.setBounds(100, 100, 160, 25);
        
        lblBuscar.setBounds(300, 20, 120, 25);
        txtBuscar.setBounds(420, 20, 150, 25);
        
        // Posicionar los botones principales en una fila horizontal
        btnAgregar.setBounds(20, 140, 100, 25);
        btnActualizar.setBounds(130, 140, 100, 25);
        btnBuscar.setBounds(300, 60, 100, 25);
        btnListar.setBounds(410, 60, 100, 25);
        btnEliminar.setBounds(20, 350, 100, 25);

        // Posicionar la tabla
        scrollTabla.setBounds(20, 180, 550, 150);

        // Agregar los componentes a la ventana
        add(lblNombre);
        add(txtNombre);
        add(lblCedula);
        add(txtCedula);
        add(lblTelefono);
        add(txtTelefono);
        add(lblBuscar);
        add(txtBuscar);
        add(btnAgregar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnBuscar);
        add(btnListar);
        add(scrollTabla);

        // Configurar acciones de los botones
        btnAgregar.addActionListener(e -> insertarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> {
            String cedula = txtCedula.getText();
            if (cedula.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una cédula.");
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de que desea eliminar al cliente con cédula " + cedula + "?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                eliminarCliente(cedula);
            }
        });
        btnBuscar.addActionListener(e -> buscarCliente());
        btnListar.addActionListener(e -> listarClientes());
    }

    private void insertarCliente() {
        String nombre = txtNombre.getText();
        String cedula = txtCedula.getText();
        String telefono = txtTelefono.getText();
        limpiarCampos();
        ejecutarSQL("INSERT INTO USUARIO (NOMBRE, CEDULA, TELEFONO) VALUES (?, ?, ?)", nombre, cedula, telefono);
        listarClientes();
    }

    private void actualizarCliente() {
        String nombre = txtNombre.getText();
        String cedula = txtCedula.getText();
        String telefono = txtTelefono.getText();
        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
        return;
    }
        limpiarCampos();
        
        String sql ="UPDATE USUARIO SET NOMBRE = ?, TELEFONO = ? WHERE CEDULA = ?";
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/passarela_queen", "root", "");
            PreparedStatement ps = conexion.prepareStatement(sql)) {
        
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, cedula);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un cliente con esa cédula.");
            }
        
            // Listar nuevamente los clientes
            listarClientes();
        } catch (SQLException ex) {
            // Imprimir el error completo para depuración
            JOptionPane.showMessageDialog(this, "Error en la operación: " + ex.getMessage());
            Logger.getLogger(RegistroCliente.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    private void eliminarCliente(String cedula) {
        String sql = "DELETE FROM USUARIO WHERE CEDULA = ?";
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/passarela_queen", "root", "");
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            
            ps.setString(1, cedula);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un cliente con esa cédula.");
            }
            limpiarCampos();
            listarClientes(); // Refrescar la tabla
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar el cliente.");
            Logger.getLogger(RegistroCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buscarCliente() {
        String cedula = txtBuscar.getText();
        //limpiarCampos();
        modeloTabla.setRowCount(0); // Limpiar la tabla
        ejecutarConsultaSQL("SELECT * FROM USUARIO WHERE CEDULA = ?", cedula);
    }

    private void listarClientes() {
        limpiarTabla();
        //modeloTabla.setRowCount(0); // Limpiar la tabla
        ejecutarConsultaSQL("SELECT * FROM USUARIO", new String[]{});
    }
    
    private void limpiarCampos() {
        // Limpiar los campos de texto
        txtNombre.setText("");
        txtCedula.setText("");
        txtTelefono.setText("");
        txtBuscar.setText("");
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0); // Elimina todas las filas pero mantiene la estructura de columnas
    }
    
    private void ejecutarSQL(String sql, String... params) {
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/passarela_queen", "root", "");
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Operación realizada con éxito.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la operación.");
            Logger.getLogger(RegistroCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ejecutarConsultaSQL(String sql, String... params) {
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/passarela_queen", "root", "");
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{rs.getString("NOMBRE"), rs.getString("CEDULA"), rs.getString("TELEFONO")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la consulta.");
            Logger.getLogger(RegistroCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistroCliente().setVisible(true));
    }
}
