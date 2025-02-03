# API-REST
Challenge para Tenpo

El objetivo de esta aplicación es  construir una API REST robusta utilizando Spring Boot, con integración de servicios externos, manejo de excepciones, registro de historial, y pruebas automatizadas.

# Levantamiento

Para la automatización del levantamiento de las distintas tecnologías utilizadas para la realización de este proyecto (Spring boot, Redis y PostgreSQL), se utilizó Docker compose.
Para el levantamiento de este proyecto se debe ejecutar el siguiente comando:

docker-compose up --build -d


Para ejecutar esta instrucción, es necesario estar en la raíz del proyecto. Esta acción genera una nueva imagen que contiene el código de la aplicación, inicia un contenedor basado en dicha imagen y, de manera similar, levanta los servicios de PostgreSQL, Redis y el servicio externo correspondiente.

Es importante destacar que la aplicación no funcionará correctamente si se intenta ejecutar directamente desde el método Main, ya que las propiedades de configuración están definidas para apuntar exclusivamente a las instancias dockerizadas de PostgreSQL, Redis y el servicio mock (encargado de proporcionar el porcentaje). Debido a esta configuración, la conexión no se establecerá correctamente fuera del entorno de Docker.

Para abordar esta limitación y garantizar la ejecución de pruebas de integración de forma independiente de Docker, se decidió utilizar Testcontainers. Esta solución permite realizar pruebas en un entorno controlado sin necesidad de depender de la infraestructura del contenedor principal.



# Componentes

## Controladores
### ApiRestController
El controlador principal de la aplicación. Maneja las solicitudes HTTP y coordina las respuestas utilizando los servicios de la aplicación. Este controlador expone dos endpoints principales:

* Endpoint /api/percentage:

Método: POST
Parámetros: num1 (float), num2 (float)
Funcionalidad: Suma de los dos números proporcionados añadiéndoles un porcentaje. Registra la solicitud y la respuesta en la base de datos.
Flujo:
* Recibe la solicitud con los parámetros num1 y num2.
* Llama a PercentageService para obtener un porcentaje.
* Aplica el porcentaje a la suma de los números proporcionados.
* Llama a RequestLogService para registrar la solicitud y la respuesta.
* Devuelve el resultado al cliente.

  
* Endpoint /api/log:

Método: GET
Parámetros: page (int), size (int)
Funcionalidad: Recupera los logs de solicitudes en formato paginado.
Flujo:
* Recibe la solicitud con los parámetros page y size.
* Llama a RequestLogService para obtener los logs de solicitudes.
* Devuelve los logs al cliente en formato paginado.


## Servicios
### PercentageService
Proporciona la funcionalidad para obtener un porcentaje y aplicarlo a dos números. Este servicio interactúa con un servicio externo para obtener el porcentaje y maneja la lógica de negocio para aplicar el porcentaje a los números proporcionados.

Método getRandomPercentage:
* Llama a un servicio externo para obtener un porcentaje.
* Si la llamada al servicio externo falla, intenta recuperar el porcentaje desde la caché de Redis.
* Devuelve el porcentaje obtenido.
 
Método sumAndAddPercentage:
* Aplica el porcentaje obtenido a los dos números proporcionados.
* Devuelve el resultado.

### RequestLogService
Gestiona el registro y la recuperación del historial de solicitudes. Este servicio se encarga de registrar cada solicitud y su respuesta en la base de datos mediante un mecanismo de apertura, actualización y cierre de solicitud. Adicionalmente, proporciona la funcionalidad para recuperar estos logs en formato paginado.

Método openRequest:
* Registra una nueva solicitud en la base de datos.
* Empieza el ciclo de vida de una solicitud recibiendo los parámetros de inicio.

Método updateResponse:
* Actualiza la respuesta de la solicitud actual.
  
Método closeRequest (Asíncrono):
* Cierra la solicitud y la registra en la base datos.
* Finaliza con el ciclo de vida de la solcitud.

Método cancelRequest:
* Descarta la solicitud actual.

Método getRequestLogs:
* Recupera el hisotorial de solicitudes en formato paginado.

### RedisService
Proporciona la funcionalidad para interactuar con Redis. Este servicio se utiliza para almacenar y recuperar datos en un almacén de datos Redis.

Método setKey:
* Guarda un valor en Redis con una clave y una TTL (en segundos) específicada.

Método getKey:
* Recupera un valor de Redis utilizando una clave específica.
  
### RateLimiterService
Gestiona la limitación de tasa para las solicitudes. Este servicio se utiliza para controlar la cantidad de solicitudes que se pueden realizar en un período de tiempo determinado, evitando así la sobrecarga del sistema.

Método initRateLimitBucket:
* Inicializa un bucket de limitación de tasa con una capacidad y tasa de llenado específicas.

Método tryConsume:
* Consume la cantidad de tokens específicados del bucket de limitación de tasa.

## Entidades
### RequestLog
Representa un log de solicitud en la base de datos. Esta entidad almacena todos los detalles relevantes de una solicitud, incluyendo la fecha y hora de la solicitud, el endpoint al que se accedió, los parámetros de la solicitud y la respuesta. Este se almacena en la tabla request_logs.

Atributos:
* id: Identificador único del log.
* requestTime: Fecha y hora de la solicitud.
* endpoint: Endpoint de la solicitud.
* params: Parámetros de la solicitud. Este se mapea a una tabla externa 'params' que se relaciona con esta mediante de una columna 'request_id'.
* response: Respuesta de la solicitud.
* responseJson: Respuesta de la solicitud en formato Json para el soporte de datos en tabla por protocolo JPA.

## DTOs
### RequestLogDTO
Objeto de transferencia de datos para RequestLog. Este DTO se utiliza para la exposición de datos de la entidad RequestLog.

Atributos:
* id: Identificador único del log.
* requestTime: Fecha y hora de la solicitud.
* endpoint: Endpoint de la solicitud.
* params: Parámetros de la solicitud.
* response: Respuesta de la solicitud.

Método fromEntity:
* Convierte una entidad RequestLog en un RequestLogDTO.

## Repositorios
### RequestLogRepository
Proporciona la funcionalidad para interactuar con la base de datos para la entidad RequestLog. Este repositorio extiende JpaRepository y proporciona métodos CRUD.

## Filtros
### RequestLogFilter
Intercepta las solicitudes HTTP para registrar los detalles de la solicitud. Este filtro se aplica únicamente a las llamdas que se realizan a la API mediante el endpoint de operación matemática y se encarga de registrar los detalles de cada solicitud utilizando RequestLogService.

Método doFilterInternal:
* Intercepta las solicitudes a /api/percentage.
* Abre la solicitud actual inicializando una instancia de RequestLog junto a los detalles de entrada, como los parámetros el momento en el que se realiza y el detalle del endpoint utilizado.
* Continúa con el procesamiento de la solicitud.
* Al finalizar el procesamiento, cierra el ciclo de vida de la instancia.

### RateLimitFilter
Interceta las solicitudes HTTP para gestionar el límite de llamadas por segundo utilizando el servicio RateLimiterService.

Método doFilterInternal:
* Consume un token.
* Continúa con el procesamiento de la solicitud.

### ExceptionFilter
Intercepta las solicitudes HTTP para gestionar las excepciones ocurridas en el flujo normal del procesamiento de la solicitud. También maneja excepciones ocurridas en otros filtros.

Método doFilterInternal:
* Continúa con el procesamiento de la solicitud.
* Captura las excepciones mapeandolas con el código de respuesta correspondiente.

Método handleExpception:
* Construye un objeto ErrorDTO a partir del mensaje recibido.
* Si ay una instancia de solicitud abierta, actualiza su respuesta con el error construido.
* Cierra la solicitud.
* Utiliza objeto response, el mensaje y el código de salida para interrumpir el flujo y dar una respuesta de error al usuario.


## Excepciones
### BadParamException
Excepción personalizada para manejar parámetros inválidos en las solicitudes.
### EmptyResponseException
Excepción personalizada para manejar resupestas vacías.
### ExternalException
Excepción personalizada para manejar respuestas que se lanzan por fuera del flujo normal de una solicitud. Como por ejemplo, un error ocurrido en el método de recuperación de fallos (@Recover).
### TooManyRequestException
Excepción personalizada para manejar el límite de tasa de solicitudes. Es lanzado cuando se excede el umbral.

## Control de errores
Las excepciones son controladas en el filtro ExcepcionFilter siempre y cuando ocurran en el flujo normal de la solicitud. Si ocurre una excepción por fuera de este, es controlada por ControllerAdvice.

### Controller Advice

Método handleException:
* Recibe un mensaje y un código de salida para crear una respuesta de error ResponseEntity<ErrorDTO>.
* Si hay una solicitud abierta, actualiza su respuesta
* Si ocurre un error en la actualización de la respuesta, cancela la solicitud y genera un nuevo error con el detalle.
* Retorna la respuesta creada.

Método handleExternalException:
* Maneja la excepción ExternalException, heredando el mensaje y código de error declarado.

Método handleMissingParams:
* Maneja la excepción MissingServletRequestParameterException, heredando el mensaje y seteando un código de error BAD_REQUEST (400).

Método handleNotFound:
* Maneja la excepción NoHandlerFoundException,  heredando el mensaje y estableciendo un código de error NOT_FOUND (404).

Método handleMethodNotAllowed:
* Maneja la excepción HttpRequestMethodNotSupportedException, heredando el mensaje y estableciendo un código de error METHOD_NOT_ALLOWED (405).

### Mapeo de errores personalizados

* TooManyRequestException -  HttpStatus.TOO_MANY_REQUESTS (429)

* EmptyResponseException - HttpStatus.BAD_GATEWAY (502)

* BadParamException - HttpStatus.BAD_REQUEST (400)

## Configuraciones
### AsyncConfig
Se realizan configuraciones iniciales relacionadas con el tratamiento de hilos y tareas asíncronas.

### CacheConfig
Se realizan configuraciones iniciales para la serialización de datos caché en redis.

### RateLimiterConfig
Se configura el límite de tasa de solicitudes de la aplicación. Se establece en 3 solicitudes por minuto.

### RestTemplateConfig
Se configura un Bean para la inyección de dependencia del objeto RestTemplate, este es útil para la conexión con el servicio externo.


### Testing
Las pruebas se realizan utilizando JUnit, Mockito, Testcontainers y WireMock. Las pruebas están ubicadas en el directorio api_rest.
