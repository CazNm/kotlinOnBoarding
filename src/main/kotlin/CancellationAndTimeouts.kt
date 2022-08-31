import kotlinx.coroutines.*
import java.lang.Exception

fun main() {
    println("Testing Cancellation and Timeout in Coroutines")
}

fun cancelCoroutine() = runBlocking {
    val job = launch {
        try {
            repeat(1000) {
                println("job : I'm sleeping $it")
                delay(500L)
            }
        }
        catch (e : Exception)
        {
            println(e)
        }
    }

    delay(1300L)
    println("main : I'm tired of waiting")
    job.cancel()
    job.join()
    println("main : Now I can quit.")
}
fun cooperativeCancelTest() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        try {
            while (i < 5) { //computation loop just wastes CPU
                //print message twice a second

                if(System.currentTimeMillis() >= nextPrintTime)
                {
                    println("job: I'm sleeping ${i++}")
                    nextPrintTime += 500L
                }
            }
        } catch (e: Exception)
        {
            println(e)
        }

        println("done")
    }

    delay(1300L)
    println("main: I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit")

}

fun checkCancelError() = runBlocking {
    val job = launch {
        repeat(5) {
            try {
                println("job : i'm sleeping $it ...")
                delay(500)
            }
            catch (e : Exception) {
                println(e)
            }
        }
    }

    delay(1300L)
    println("main: I'm tired of waiting!")
    job.cancel()
    job.join()
    println("main: Now I can quit")
}

fun cancelComputationCoroutine () = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) { //cancelable coroutine loop
            //print message twice a second

            if(System.currentTimeMillis() >= nextPrintTime)
            {
                println("job: i'm sleeping ${i++}")
                nextPrintTime += 500L
            }

        }
    }

    delay(1300L)
    println("main : I'm tired of waiting!")
    job.cancelAndJoin()
    println("main : Now i can quit.")
}

fun closeResourceWithFinally() = runBlocking {
    val job = launch {
        try {
            repeat(1000) {
                println("job: I'm sleeping $it ...")
                delay(500L)
            }
        }
        finally {
            println("job : I'm running finally")
        }
    }

    delay(1300L)
    println("main : I'm tired of waiting")
    job.cancelAndJoin()
    println("main : Now I can quit.")
}

fun nonCancellableBlock() = runBlocking {
    val job = launch {
        try {
            repeat(1000)
            {
                println("job: I'm sleeping $it ...")
                delay(500L)
            }
        } finally {
            //일반적인 Dispathcer를 사용하면 cancel이 되지만, NonCancellable을 Context로
            //넘겨줌으로써 Suspend Function이 필요한 처리의 경우 아래와 같이 사용할 수 있다.
            withContext(NonCancellable){
                println("job: I'm running finally")
                delay(1000L)
                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
    }

    delay(1300L)
    println("main: I'm tired of waiting")
    job.cancelAndJoin()
    println("main: Now I can quit")
}

fun coroutineTimeOut() = runBlocking {
    withTimeout(1300L) {
        repeat(1000) {
            println("I'm sleeping $it ...")
            delay(500L)
        }
    } //kotlinx.coroutines.TimeoutCancellationException: TimeoutExeption 은 CancellationException 의 하위 클래스 이다.
}

fun coroutineTimeOutOrNull() = runBlocking {
    val result = withTimeoutOrNull(1300L) {
        repeat(1000) {
            println("I'm sleeping $it ...")
            delay(500L)
        }
        "Done"
    }

    println("Result is $result")
}

var acquired = 0

class Resource {
    init {
        acquired++
    }

    fun close() { acquired--}
}

//2 가지의 차이점을 구분할 줄 알아야한다. 왜? 다를까

fun asynchronusAndResource()= runBlocking {
    repeat(100_000) {
        launch {
            val resource = withTimeout(60)
            {
                delay(50)
                Resource()
            }
            resource.close()
        }
    }

    println(acquired)
}// 이 함수는 항상 0의 값을 보장하지 않는다.

fun asynchronusAndResourcePreventNonZero() = runBlocking {
    repeat(100_100) {
        launch {
            var resource : Resource? = null
            try {
                withTimeout(60)
                {
                    delay(50)
                    resource = Resource()
                }
            }
            finally {
                resource?.close()
            }
        }
    }

    println(acquired)
}// 이 함수는 0의 값을 보장한다... 무슨 차이가 있길래?