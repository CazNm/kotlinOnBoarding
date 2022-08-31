import kotlinx.coroutines.*

fun main() {
//    debuggingWithLog()
//    jumpingBetweenThreads()
//    jobInTheContext()
//    childrenOfCoroutine()
//    parentalResponsibilities()
    namingCoroutineForDebug()
}

fun log(msg : String) = println("[${Thread.currentThread().name}] $msg")

fun debuggingWithLog() = runBlocking {
    val a = async {
        log("I'm computing a piece of the answer")
        6
    }

    val b = async {
        log("I'm computing a another piece of the answer")
        7
    }

    log ("The answwer is ${a.await() * b.await()}")

}



fun jumpingBetweenThreads()  {
    newSingleThreadContext("Ctx1").use { ctx1 ->
        newSingleThreadContext("Ctx2").use {
            ctx2 ->
            runBlocking(ctx1) {
                log("Started in ctx1")
                withContext(ctx2) {
                    log("Working in ctx2")
                }
                log("Back to ctx1")
            }
        }
    }
}
//newSingleThreadContext 는 내가 정의한 CoroutineDispatcher 를 만들어 주는 것 같다.


fun jobInTheContext() = runBlocking {
    println("My job is ${coroutineContext[Job]}")
}

//coroutineScope 에서 접근 가능한 isActive boolean 은 coroutineContext[Job]?.isActive == true 의 축약형이다.

fun childrenOfCoroutine() = runBlocking {
    val request = launch {
        launch(Job()) {
            println("job1: I run in my own Job and execute independently!")
            delay(1000L)
            println("job1: I am not affected by cancellation of the request")
        }

        launch {
            delay(100)
            println("job2: I am child of the request coroutine")
            delay(1000L)
            println("job2: I will not execute this line if my parent request is cancelled")
        }
    }

    delay(500)
    request.cancel()
    println("main: Who has survived request cancellation?")
    delay(1000L)
}

fun parentalResponsibilities() = runBlocking {
    val request = launch {
        repeat(3) {
            launch {
                delay((it+1)* 200L)
                println("Coroutine $it is done")
            }
        }

        println("request: I'm done and I don't explicitly join my children that are still active")
    }

    request.join()
    println("Now processing of the request is complete")
} //부모 coroutine 은 각각의 자식 coroutine 을 끝나는 것을 일일히 확인하지 않아도 된다. 모든 자식 coroutine 이 종료되면, 부모 coroutine 또한 종료된다.

fun namingCoroutineForDebug() = runBlocking {
    log("Started main Coroutine")

    val v1 = async(CoroutineName("v1Coroutine")) {
        delay(500)
        log("Computing V1")
        252
    }

    val v2 = async(CoroutineName("v2Coroutine")) {
        delay(1000)
        log("Computing V2")
        6
    }


    log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
}