package logic;

import java.util.ArrayList;
import model.Casa;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;

public class Condominio {
    private ArrayList<Casa> listaCasas;

    public Condominio() {
        this.listaCasas = new ArrayList<>();
        // Al iniciar, cargamos las 30 casas desde la DB
        cargarCasasDesdeBD();
    }

    private void cargarCasasDesdeBD() {
        // Consulta para obtener las casas de tu tabla SQLite
        String sql = "SELECT * FROM Casas ORDER BY numero_casa ASC";
        
        try (Connection conn = ConexionDB.getConexion(); // Asegúrate que tu método sea static
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // El constructor : public Casa(int numero_casa)
                Casa nuevaCasa = new Casa(rs.getInt("numero_casa"));
                
               
                // Setters
                
                listaCasas.add(nuevaCasa);
            }
            System.out.println("LOG: 30 casas cargadas exitosamente.");
            
        } catch (SQLException e) {
            System.err.println("ERROR al cargar desde DB: " + e.getMessage());
        }
    }

    public ArrayList<Casa> getListaCasas() {
        return listaCasas;
    }
 public static void main(String[] args) {
        
        // 1. Activamos FlatLaf para que todo el sistema de condominios sea moderno
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Error al inicializar FlatLaf: " + ex.getMessage());
        }

        // 2. Aquí llamamos a la ventana de los condominios
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // REEMPLAZA 'VentanaCondominios' por el nombre real de tu JFrame 
                // (por ejemplo: MenuPrincipal, Login, o FrmCondominio)
                new VentanaCondominios().setVisible(true); 
            }
        });
    }
}
}
