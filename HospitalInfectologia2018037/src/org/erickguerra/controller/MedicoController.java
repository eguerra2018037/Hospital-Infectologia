package org.erickguerra.controller;

import static com.sun.javafx.tk.Toolkit.getToolkit;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.erickguerra.bean.Medico;
import org.erickguerra.db.Conexion;
import org.erickguerra.report.GenerarReporte;
import org.erickguerra.sistema.Principal;


public class MedicoController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO}
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Medico> listaMedico;
    
    @FXML private TextField txtLicenciaMedica;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtHoraEntrada;
    @FXML private TextField txtHoraSalida;
    @FXML private TextField txtTurno;
    //@FXML private TextField txtSexo;
    @FXML private TableView tblMedicos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colLicenciaMedica;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colEntrada;
    @FXML private TableColumn colSalida;
    @FXML private TableColumn colTurno;
    @FXML private TableColumn colSexo;
    @FXML private ComboBox cmbSexo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        cargarDatos();
        cmbSexo.setItems(FXCollections.observableArrayList("Femenino","Masculino","Otro"));
    }
    
    public void cargarDatos(){
        tblMedicos.setItems(getMedicos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("codigoMedico"));
        colLicenciaMedica.setCellValueFactory(new PropertyValueFactory<Medico, Long>("licenciaMedica"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Medico,String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Medico,String>("apellidos"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<Medico, String>("horaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<Medico, String>("horaSalida"));
        colTurno.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("turnoMaximo"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Medico, String>("sexo"));
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos}");
                ResultSet resultado = procedimiento.executeQuery();
                while(resultado.next()){
                    lista.add(new Medico(resultado.getInt("codigoMedico"),
                        resultado.getLong("licenciaMedica"),
                        resultado.getString("nombres"),
                        resultado.getString("apellidos"),
                        resultado.getString("horaEntrada"),
                        resultado.getString("horaSalida"),
                        resultado.getInt("turnoMaximo"),
                        resultado.getString("sexo")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return listaMedico = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblMedicos.getSelectionModel().isEmpty()){
            tblMedicos.getSelectionModel();
        }else{
            txtLicenciaMedica.setText(String.valueOf(((Medico) tblMedicos.getSelectionModel().getSelectedItem()).getLicenciaMedica()));
            txtNombres.setText(((Medico) tblMedicos.getSelectionModel().getSelectedItem()).getNombres());
            txtApellidos.setText(((Medico) tblMedicos.getSelectionModel().getSelectedItem()).getApellidos());
           // txtSexo.setText(((Medico) tblMedicos.getSelectionModel().getSelectedItem()).getSexo());
            cmbSexo.getSelectionModel().select(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getSexo());
            txtHoraEntrada.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraEntrada());
            txtHoraSalida.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraSalida());
            txtTurno.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getTurnoMaximo()));
            
        }
    }
        
    public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
                procedimiento.setInt(1, codigoMedico);
                ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new Medico(registro.getInt("codigoMedico"),
                            registro.getLong("licenciaMedica"),
                            registro.getString("nombres"),
                            registro.getString("apellidos"),
                            registro.getString("horaEntrada"),
                            registro.getString("horaSalida"),
                            registro.getInt("turnoMaximo"),
                            registro.getString("sexo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                tblMedicos.setDisable(false);
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            default:
                seleccionarElemento();
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de que desea eliminar el registro?", "Eliminar Medico", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico(?)}");
                            procedimiento.setInt(1, ((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
                            procedimiento.execute();
                            listaMedico.remove(tblMedicos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento.");
                }
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                tblMedicos.setDisable(true);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean validar = validacionLicencia(txtLicenciaMedica.getText());
                if(txtLicenciaMedica.getText().equals("") || txtNombres.getText().equals("")|| txtApellidos.getText().equals("") 
                    || txtHoraEntrada.getText().equals("") || txtHoraSalida.getText().equals("") || /*txtSexo.getText().equals("")*/ 
                        cmbSexo.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validar == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente.");
                    txtLicenciaMedica.setText("");
                }else if(txtNombres.getText().length() > 100){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres.");
                    txtNombres.setText("");
                }else if(txtApellidos.getText().length() > 100){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres.");
                    txtApellidos.setText("");
               }else if(txtHoraEntrada.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraEntrada.setText("");
                }else if(txtHoraSalida.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraSalida.setText("");
                }else if(txtLicenciaMedica.getText().length()!=10){
                    JOptionPane.showMessageDialog(null, "La licencia ingresada no es válida");
                    txtLicenciaMedica.setText("");
                }else if(!(txtHoraEntrada.getText().substring(2,3)).equals(":") && !(txtHoraEntrada.getText().substring(5,6)).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraEntrada.setText("");
                }else if(!(txtHoraSalida.getText().substring(2,3)).equals(":") && !(txtHoraSalida.getText().substring(5,6)).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraSalida.setText("");
                }else if((Integer.parseInt(txtHoraEntrada.getText().substring(0,2))>24) || (Integer.parseInt(txtHoraEntrada.getText().substring(3,5))>=60)
                        || (Integer.parseInt(txtHoraEntrada.getText().substring(6,8))>=60)){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraEntrada.setText("");
                }else if((Integer.parseInt(txtHoraSalida.getText().substring(0,2))>24) || (Integer.parseInt(txtHoraSalida.getText().substring(3,5))>=60)
                        || (Integer.parseInt(txtHoraSalida.getText().substring(6,8))>=60)){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraSalida.setText("");
                }else{
                    boolean validarHoraEntrada = validacionHoras(txtHoraEntrada.getText().substring(0, 2), txtHoraEntrada.getText().substring(3, 5), 
                        txtHoraEntrada.getText().substring(6, 8));
                    boolean validarHoraSalida = validacionHoras(txtHoraSalida.getText().substring(0, 2), txtHoraSalida.getText().substring(3, 5), 
                        txtHoraSalida.getText().substring(6, 8));
                    if(validarHoraEntrada == false){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraEntrada.setText("");
                    }else if(validarHoraSalida == false){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraSalida.setText("");
                    }else{
                    guardar();
                    desactivarControles();
                    tblMedicos.setDisable(false);
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();}
                }break;
        } 
    }
    
    public void generarReporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            case NINGUNO:
                imprimirReporte();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
        }
    }
    
    public void guardar(){
        Medico registro = new Medico();
        registro.setLicenciaMedica(Long.parseLong(txtLicenciaMedica.getText()));
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setHoraEntrada(txtHoraEntrada.getText());
        registro.setHoraSalida(txtHoraSalida.getText());
        //registro.setSexo(txtSexo.getText());
        registro.setSexo(cmbSexo.getSelectionModel().getSelectedItem().toString());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedico(?,?,?,?,?,?)}");
            procedimiento.setLong(1, registro.getLicenciaMedica());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getHoraEntrada());
            procedimiento.setString(5, registro.getHoraSalida());
            procedimiento.setString(6, registro.getSexo());
            procedimiento.execute();
            listaMedico.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    seleccionarElemento();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }break;
            case ACTUALIZAR:
                boolean validar = validacionLicencia(txtLicenciaMedica.getText());
                if(txtLicenciaMedica.getText().equals("") || txtNombres.getText().equals("")|| txtApellidos.getText().equals("") 
                    || txtHoraEntrada.getText().equals("") || txtHoraSalida.getText().equals("") || /*txtSexo.getText().equals("")*/
                        cmbSexo.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validar == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente.");
                    txtLicenciaMedica.setText("");
                }else if(txtNombres.getText().length() > 100){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres.");
                    txtNombres.setText("");
                }else if(txtApellidos.getText().length() > 100){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres.");
                    txtApellidos.setText("");
                }else if(txtHoraEntrada.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraEntrada.setText("");
                }else if(txtHoraSalida.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraSalida.setText("");
                }else if(txtLicenciaMedica.getText().length()!=10){
                    JOptionPane.showMessageDialog(null, "La licencia ingresada no es válida");
                    txtLicenciaMedica.setText("");
                }else if(!(txtHoraEntrada.getText().substring(2,3)).equals(":") && !(txtHoraEntrada.getText().substring(5,6)).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraEntrada.setText("");
                }else if(!(txtHoraSalida.getText().substring(2,3)).equals(":") && !(txtHoraSalida.getText().substring(5,6)).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraSalida.setText("");
                }else if((Integer.parseInt(txtHoraEntrada.getText().substring(0,2))>24) || (Integer.parseInt(txtHoraEntrada.getText().substring(3,5))>=60)
                        || (Integer.parseInt(txtHoraEntrada.getText().substring(6,8))>=60)){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraEntrada.setText("");
                }else if((Integer.parseInt(txtHoraSalida.getText().substring(0,2))>24) || (Integer.parseInt(txtHoraSalida.getText().substring(3,5))>=60)
                        || (Integer.parseInt(txtHoraSalida.getText().substring(6,8))>=60)){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraSalida.setText("");
                }else{
                    boolean validarHoraEntrada = validacionHoras(txtHoraEntrada.getText().substring(0, 2), txtHoraEntrada.getText().substring(3, 5), 
                        txtHoraEntrada.getText().substring(6, 8));
                    boolean validarHoraSalida = validacionHoras(txtHoraSalida.getText().substring(0, 2), txtHoraSalida.getText().substring(3, 5), 
                        txtHoraSalida.getText().substring(6, 8));
                    if(validarHoraEntrada == false){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraEntrada.setText("");
                    }else if(validarHoraSalida == false){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraSalida.setText("");
                    }else{
                    actualizar();
                    desactivarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();}
                }break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarMedico(?,?,?,?,?,?,?)}");
            Medico registro = (Medico)tblMedicos.getSelectionModel().getSelectedItem();
            registro.setLicenciaMedica(Long.parseLong(txtLicenciaMedica.getText()));
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setHoraEntrada(txtHoraEntrada.getText());
            registro.setHoraSalida(txtHoraSalida.getText());
            //registro.setSexo(txtSexo.getText());
            registro.setSexo(cmbSexo.getSelectionModel().getSelectedItem().toString());
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setLong(2, registro.getLicenciaMedica());
            procedimiento.setString(3, registro.getNombres());
            procedimiento.setString(4, registro.getApellidos());
            procedimiento.setString(5, registro.getHoraEntrada());
            procedimiento.setString(6, registro.getHoraSalida());
            procedimiento.setString(7, registro.getSexo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean validacionLicencia(String licencia){
        long caracter = 0;
        try{
            caracter = Long.parseLong(licencia);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public boolean validacionHoras(String hora, String minuto, String segundo){
        int horas = 0;
        int minutos = 0;
        int segundos = 0;
        try{
            horas = Integer.parseInt(hora);
            minutos = Integer.parseInt(minuto);
            segundos = Integer.parseInt(segundo);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoReporte", null);
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros);
    }
    
    public void desactivarControles(){
        txtLicenciaMedica.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtHoraEntrada.setEditable(false);
        txtHoraSalida.setEditable(false);
        txtTurno.setEditable(false);
        //txtSexo.setEditable(false);
        cmbSexo.setDisable(true);
    }
    
    public void activarControles(){
        txtLicenciaMedica.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtHoraEntrada.setEditable(true);
        txtHoraSalida.setEditable(true);
        txtTurno.setEditable(false);
        //txtSexo.setEditable(true);
        cmbSexo.setDisable(false);
    }
    
    public void limpiarControles(){
        switch(tipoDeOperacion){
            case NINGUNO:
                txtLicenciaMedica.setText("");
                txtNombres.setText("");
                txtApellidos.setText("");
                txtHoraEntrada.setText("");
                txtHoraSalida.setText("");
                txtTurno.setText("");
                //txtSexo.setText("");
                cmbSexo.getSelectionModel().select(null);
                tblMedicos.getSelectionModel().clearSelection();
                break;
        }
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaTelefonoMedicos(){
        escenarioPrincipal.ventanaTelefonosMedicos();
    }
    
    public void ventanaMedicoEspecialidad(){
        escenarioPrincipal.ventanaMedicoEspecialidad();
    }
    
    public void ventanaHorarios(){
        escenarioPrincipal.ventanaHorarios();
    }
}