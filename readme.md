# Sistema a traves de correo para Gestión de Historias Clínicas para "MediTeam"

Proyecto desarrollado en Java 11 con el administrador de dependecias Maven

## Prerequisitos para el desarrollo
- Eclipse IDE. En caso de solo desarrollar en eclipse con eso bastaria - [Descarga](https://www.eclipse.org/downloads/packages/release/2026-03/r/eclipse-ide-java-developers) 
- Java Development Kit 11 (JDK11) - [Descarga](https://adoptium.net/temurin/releases/?version=11)
- Maven Package Manager - [Descarga](https://maven.apache.org/download.cgi)
- Git - [Descarga](https://git-scm.com/install/)

## Desarrollo
1. Clona el proyecto

```bash
git clone https://github.com/CesarKnight/MediTeamMail.git
```

2. Entra a la carpeta

3. Copia el duplica el archivo ".env copy" como ".env" y escribe las variables mencionadas en el archivo

```
# MAILSERVER
MAIL_SERVER="mail.server.com"
MAIL_TO_LISTEN="mailwithoutad"
PASSWORD="password"

# DATABASE
DB_DIALECT="POSTGRESQL"
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="mediteam"
DB_USER="postgres"
DB_PASSWORD="password"

# APP CONFIG
COMANDO_EJECUCION="mediteam"
MAIL_SYNC_INTERVAL_MS=10000
```

### Preparacion del proyecto en Eclipse

En caso de comenzar el desarrollo usando el IDE eclipse

1. En el menu File. Presionar "Open project from file system" y abrir la carpeta del proyecto
2. Presionar click-derecho en la carpeta de proyecto, selecionar Maven > Update Project
3. Tickear la casilla de "Force Update of Snapshots/Releases"
4. Seleccionar Ok

### Preparacion con terminal

En el caso de usar algun editor de codigo como VsCode y un terminal para el desarrollo 

1. Entra a la carpeta del proyecto
   ```bash
   cd MediTeamMail/
   ```

2. Descarga las dependencias con Maven
   ```bash
   mvn dependency:resolve
   ```
   Este comando descarga las dependencias en tu carpeta $(usuario)/.m2 y las llama en el proyecto

## Ejecución y compilación

### Eclipse

En el IDE Eclipse para la **ejecución** basta con presionar F6, lo cual temporalmente compilará y ejecutará.

Para la **compilacion** en un archivo .jar es necesario

1. Presionar click-derecho en la carpeta de proyecto, selecionar ¨Run as..." > "Maven build"
2. En el menu de Maven Build, en la seccion Goals pegar
   ```
   clean compile assembly:single
   ```

El resultado será una carpeta target/ la cual contendrá el archivo MediTeamMail-1.0-jar-with-dependencies.jar.

Para ejecución se debe usar la terminal

### Terminal

La ejecución directa toma muchas lineas por lo que mas rapido sale compilar a Jar.

Usar maven para la compilacion
```bash
mvn clean compile assembly:single
```
El resultado será una carpeta target/ la cual contendrá el archivo MediTeamMail-1.0-jar-with-dependencies.jar.

Para su ejecución, en la misma carpeta debe estar el archivo ".env" con las credenciales necesarias.
Correr con:
```bash
java -jar MediTeamMail-1.0-jar-with-dependencies.jar
```

### Ejecución en VSCode

Afortunadamente Vscode con las extensiones necesarias viene con un boton de ejecución de codigo java, F5 es el atajo a dicha funcion

Mis extensiones personales para el desarrollo en java:
 
- Debugger for Java
- Gradle for Java
- Language Support for Java(TM) by Red Hat
- Maven for Java
- Extension Pack for Java
- Project Manager for Java
- Spring Boot Dashboard
- Spring Boot Extension Pack
- Spring Boot Tools
- Spring Initializr Java Support
- Test Runner for Java
- Prettier - Code formatter 

- Git Graph 
- Git History
- GitHub Pull Requests
- Live share 
- Better Comments 
- Material Icon Theme All rofiles
- Open in GitHub
- Paddy Color Themes 
- Paste JSON as Code 
- Theme 
- TODO Highlight AllProfiles

Gracias a la extension Debugger for java podemos correr el proyecto con un solo boton, no olvides que en la carpeta base del proyecto debe estar el archivo ".env"


