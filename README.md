# SIMULACION 2018 - TP3
[![Build Status](https://circleci.com/gh/GonzaloSaad/sim-tp-2018.png?circle-token=:circle-token)](https://circleci.com/gh/GonzaloSaad/sim-tp-2018)


### Crear un proyecto de IntelliJ con JavaFX y Maven

1. Crear un proyecto JavaFX como lo haría normalmente.
2. Hacer clic derecho en el proyecto e ir a "Add Framework Support...".
3. Marque la casilla de Maven y haga clic en "Aceptar". Dejar que IntelliJ haga el trabajo. 
Aca deberia aparecer un archivo pom.xml en la raiz del projecto

4. Para editar los archivos FXML, vincular IntelliJ a SceneBuilder. 
- Instalar Scene Builder.
- File -> Settings -> Languages & Frameworks -> JavaFX
- Señalar el archivo exe.
- Para usar: hacer click con el botón derecho en el archivo FXML y 
seleccione "Abrir en SceneBuilder" (muy abajo en la parte inferior)

Mas info: 
https://stackoverflow.com/questions/30488223/how-to-create-a-javafx-maven-project-in-intellij-idea

### Buildear el proyecto. (Maven 3.5.4)

En la consola: 
```
mvn clean install
```

### Ejecutar el programa

En la carpeta `./target` existe un jar 
ejecutable llamado `sim-tp3-2018-1.0.jar`. Hacer doble click sobre el archivo.



