
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch


### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.
![](img/Imagen2.png)
2. Complete el método __main__ de la clase CountMainThreads para que:
	1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].
	2. Inicie los tres hilos con 'start()'.
	![](img/Imagen1.png)
	3. Ejecute y revise la salida por pantalla. 
	![](img/Imagen3.png)
	4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.
	![](img/Imagen4.png)
	Va cambiar la salida ya que cuando ocurre el start lo hace de forma paralela, está ejercutando varios procesos al tiempo, mientras que correrlo con el run(), se ejecuta proceso por proceso, esto hace que los números se impriman de forma ordenada
	![](img/Imagen5.png)

**Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.

![](img/Imagen6.png)

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

	* Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.

	* Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.

![](img/Imagen7.png)
![](img/Imagen8.png)

**Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?

R/ La estrategia actual de realizar búsquedas paralelas hasta que todos los hilos hayan completado su tarea puede resultar ineficiente, especialmente cuando el objetivo es encontrar un número mínimo de ocurrencias. Continuar buscando después de alcanzar este umbral implica un gasto innecesario de recursos computacionales.

Para optimizar el proceso, podemos introducir un mecanismo que permita detener la ejecución de todos los hilos tan pronto como se detecte que se ha alcanzado el número mínimo de ocurrencias requeridas.

Ref: [Optimizing parallel search with early termination in Java](https://stackoverflow.com/questions/46237721/java-utilize-multiple-ip-for-web-crawl-using-threads)

**Parte III - Evaluación de Desempeño**

A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):

1. Un solo hilo.

![](img/1hilo.png)
![](img/1hiloJV.png)

2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)).

![](img/2hilo.png)
![](img/2hiloJV.png)

3. Tantos hilos como el doble de núcleos de procesamiento.

![](img/5hilo.png)
![](img/5hiloJV.png)

4. 50 hilos.
![](img/3hilo.png)
![](img/3hiloJV.png)

5. 100 hilos.
![](img/4hilo.png)
![](img/4hiloJV.png)

Al iniciar el programa ejecute el monitor jVisualVM, y a medida que corran las pruebas, revise y anote el consumo de CPU y de memoria en cada caso. ![](img/jvisualvm.png)

Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):

Se puede llegar a la hipotesis de :
a. Aumento inicial del rendimiento: Al agregar más hilos, se espera que el tiempo de ejecución disminuya debido a la paralelización.
b. Disminución del rendimiento a partir de cierto punto: Un exceso de hilos puede generar sobrecarga en el sistema, aumentando el tiempo de contexto y disminuyendo la eficiencia.
c. Mayor consumo de memoria: Al aumentar el número de hilos, se espera un mayor consumo de memoria debido a las estructuras de datos y las pilas de cada hilo.

La gráfica se veria así: 
![](img/Incremento.png)

De pendiendo de cada hilo va tener un aumento progresivo según los hilos que tengan va ocupar más memoria 100 hilos a 1 solo hilo únicamente

ref: [Java performance tuning multithreadin](https://stackoverflow.com/questions/46237721/java-utilize-multiple-ip-for-web-crawl-using-threads)

**Parte IV - Ejercicio Black List Search**

1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):

	![](img/ahmdahls.png), donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. Por qué el mejor desempeño no se logra con los 500 hilos?, cómo se compara este desempeño cuando se usan 200?. 

R/ La Ley de Amdahl establece un límite teórico a la mejora en el rendimiento que se puede obtener al paralelizar un programa. Este límite está determinado por la porción secuencial del programa, entonces el problema de implementar los 500 hilos es principaalmente por la saturación de recursos, ya que al aumentar el número de hilos más allá de cierto punto puede saturar los recursos del sistema, como la memoria o el bus del sistema. Esto puede llevar a una disminución en el rendimiento debido a la contención por estos recursos.

2. Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.

R/ Usar tantos hilos como núcleos generalmente ofrece un buen equilibrio entre paralelización y overhead. Cada núcleo puede ejecutar un hilo de forma eficiente, maximizando el uso de los recursos del procesador, mientras que usar el doble de hilos que núcleos puede llevar a una disminución en el rendimiento debido a la sobrecarga de contexto. El sistema operativo tendrá que cambiar constantemente entre los hilos, lo que puede generar una latencia significativa. Además, puede haber una mayor contención por los recursos compartidos, como la caché.

3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas, la ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas), se mejoraría?. Explique su respuesta.

R/ La elección del número óptimo de hilos y la distribución de la carga de trabajo dependen de varios factores, como:
- La elección del número óptimo de hilos y la distribución de la carga de trabajo es un proceso iterativo que requiere un profundo conocimiento del problema, del hardware y del software. Al combinar pruebas experimentales con un análisis cuidadoso de los resultados, es posible lograr una alta eficiencia en la ejecución de aplicaciones paralelas.

Ref = [Comparación entre la Ley de Amdahl y la Ley de Gustafson](chrome-extension://efaidnbmnnnibpcajpcglclefindmkaj/https://www.uv.es/varnau/OC_T4.pdf)
[Ley de Amdahl limitaciones](https://hackernoon.com/lang/es/una-inmersion-profunda-en-la-ley-de-amdahls-y-la-ley-de-gustafson)


