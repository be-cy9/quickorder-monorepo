#!/usr/bin/env python3
"""
Agrega logging (log.info) a todos los controllers de los microservicios.
Detecta los métodos existentes e inserta log.info al inicio de cada uno.
"""

import os
import re

# Controllers a modificar (los que NO tienen logging aún)
CONTROLLERS = [
    "ms-clientes/src/main/java/com/duoc/quickorder/msclientes/controller/ClienteController.java",
    "ms-envios/src/main/java/com/duoc/quickorder/msenvios/controller/EnvioController.java",
    "ms-facturacion/src/main/java/com/duoc/quickorder/msfacturacion/controller/FacturaController.java",
    "ms-inventario/src/main/java/com/duoc/quickorder/msinventario/controller/ProductoController.java",
    "ms-notificaciones/src/main/java/com/duoc/quickorder/msnotificaciones/controller/NotificacionController.java",
    "ms-pagos/src/main/java/com/duoc/quickorder/mspagos/controller/PagoController.java",
    "ms-reportes/src/main/java/com/duoc/quickorder/msreportes/controller/ReporteController.java",
    "ms-soporte/src/main/java/com/duoc/quickorder/mssoporte/controller/TicketController.java",
    "ms-seguridad/src/main/java/com/duoc/quickorder/msseguridad/controller/UsuarioController.java",
    "ms-pedidos/src/main/java/com/duoc/quickorder/mspedidos/controller/PedidoController.java",
]

BASE = "/home/benyi/Descargas/quickorder-monorepo-main"

for rel_path in CONTROLLERS:
    filepath = os.path.join(BASE, rel_path)
    if not os.path.exists(filepath):
        print(f"[SKIP] No existe: {filepath}")
        continue

    with open(filepath, "r") as f:
        content = f.read()

    # Si ya tiene Logger, saltar
    if "LoggerFactory" in content or "log.info" in content:
        print(f"[SKIP] Ya tiene logging: {rel_path}")
        continue

    # Extraer nombre de la clase
    class_match = re.search(r'public class (\w+)', content)
    if not class_match:
        print(f"[SKIP] No encontré clase en: {rel_path}")
        continue
    class_name = class_match.group(1)

    # Agregar import de SLF4J si no está
    import_slf4j = "import org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;\n"
    if "import org.slf4j" not in content:
        # Insertar antes de la primera línea de import
        first_import = content.find("import ")
        content = content[:first_import] + import_slf4j + content[first_import:]

    # Agregar la declaración del logger después de la apertura de la clase
    class_open = re.search(r'(public class ' + class_name + r'[^{]*\{)', content)
    if class_open:
        insert_pos = class_open.end()
        logger_decl = f'\n\n    private static final Logger log = LoggerFactory.getLogger({class_name}.class);\n'
        if "LoggerFactory.getLogger" not in content:
            content = content[:insert_pos] + logger_decl + content[insert_pos:]

    with open(filepath, "w") as f:
        f.write(content)

    print(f"[OK] Logger agregado: {rel_path}")

print("\nDone!")
