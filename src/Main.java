import java.sql.*;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/empresa";
    private static final String USER = "mysql";
    private static final String PASSWORD = "mysql";

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);  //

            try {
                // consulta 1
                String query1 = "SELECT Nombre, Cargo, Salario FROM Empleado " +
                        "WHERE DepartamentoID = (SELECT DepartamentoID FROM Departamento WHERE Nombre = 'Ventas') " +
                        "AND Cargo LIKE ?";

                try (PreparedStatement pstmt = conn.prepareStatement(query1)) {
                    pstmt.setString(1, "%Asistente%");
                    try (ResultSet rs = pstmt.executeQuery()) {
                        System.out.println("Empleados del departamento de Ventas con cargo de Asistente:");
                        while (rs.next()) {
                            System.out.printf("Nombre: %s, Cargo: %s, Salario: %.2f\n",
                                    rs.getString("Nombre"),
                                    rs.getString("Cargo"),
                                    rs.getDouble("Salario"));
                        }
                    }
                }

                // consulta 2
                String query2 = "INSERT INTO Empleado (EmpleadoID, Nombre, Cargo, Salario, DepartamentoID) " +
                        "VALUES (?, ?, ?, ?, (SELECT DepartamentoID FROM Departamento WHERE Nombre = ?))";

                try (PreparedStatement pstmt = conn.prepareStatement(query2)) {
                    pstmt.setInt(1, 106);
                    pstmt.setString(2, "Laura Sánchez");
                    pstmt.setString(3, "Diseñadora Gráfica");
                    pstmt.setDouble(4, 55000.00);
                    pstmt.setString(5, "Desarrollo");

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.printf("\n%d fila(s) insertada(s)\n", rowsAffected);
                }

                // consulta 3
                String query3 = "UPDATE Empleado SET Cargo = ?, Salario = Salario * 1.15 " +
                        "WHERE Nombre = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(query3)) {
                    pstmt.setString(1, "Desarrolladora Senior");
                    pstmt.setString(2, "Ana García");

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.printf("\n%d fila(s) actualizada(s)\n", rowsAffected);
                }

                // consulta 4
                String query4 = "DELETE FROM Departamento WHERE Nombre = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(query4)) {
                    pstmt.setString(1, "Marketing");

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.printf("\n%d fila(s) eliminada(s)\n", rowsAffected);
                }

                conn.commit();
                System.out.println("\nTodas las operaciones se completaron con éxito.");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error en la transacción. Se ha revertido.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos.");
            e.printStackTrace();
        }
    }
}