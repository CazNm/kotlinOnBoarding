import kotlinx.coroutines.*
import kotlin.ArithmeticException
import kotlin.system.measureTimeMillis


fun main() {
//    sequentialByDefault()
//    sequentialWithAsync()
//    sequentialLazyAsync()
//    sequentialAsyncStyle()
//    sequentialStructuredConcurrencyWithAsync()
    cancelConcurrentSum()
}

suspend fun doSomethingUsefulOne() : Int {
    delay(1000L)
    return 13
}

suspend fun doSomethingUsefulTwo() : Int {
    delay(1000L)
    return 29
}

@OptIn(DelicateCoroutinesApi::class)
fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

@OptIn(DelicateCoroutinesApi::class)
fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

fun sequentialByDefault () = runBlocking {
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
        println("The answer is ${one + two}")
    }

    println("Completed in $time ms")
}

fun sequentialWithAsync () = runBlocking {
    val time = measureTimeMillis {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        println("The answer is ${one.await() + two.await()}")
    }

    println("Completed in $time ms")
}

fun sequentialLazyAsync()  = runBlocking {
    val time = measureTimeMillis {
        val one = async ( start = CoroutineStart.LAZY ) { doSomethingUsefulOne() }
        val two = async ( start = CoroutineStart.LAZY ) { doSomethingUsefulTwo() }

        one.start()
        two.start()
        println("The answer is ${one.await() + two.await()}")
    }

    println("Completed in $time ms")
}

fun sequentialAsyncStyle()  {
    val time = measureTimeMillis {
        val one = somethingUsefulOneAsync()
        val two = somethingUsefulOneAsync()

        runBlocking {
            println("The answer is ${one.await() + two.await()}")
        }
    }

    println("Completed in $time ms")
}
// 해당 함수는 async 함수가 동작중일 때, await 이전에 에러가 발생하게 되면, 프로그램은 중단 되지만 Async 프로세스는 그대로 진행되는 문제가 있다.
//이를 해결하기 위해서 아래의 Structured concurrency with Async 기법을 사용하자

suspend fun concurrentSum() : Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}

fun sequentialStructuredConcurrencyWithAsync() = runBlocking {
    val time = measureTimeMillis {
        println("The answer is ${concurrentSum()}")
    }

    println("Completed in $time ms")
}

//Cancellation 은 Coroutine 구조에서는 항상 전달될 수 있다.

fun cancelConcurrentSum() = runBlocking {
    try{
        failedConcurrentSum()
    }
    catch(e: ArithmeticException)
    {
        println("Computation failed with ArithmeticException")
    }
}

suspend fun failedConcurrentSum() : Int = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE)
            42
        } finally {
            println("First child was canceld")
        }
    }

    val two = async<Int> {
        println("Second child throws an exception")
        throw ArithmeticException()
    }

    one.await() + two.await()
}


