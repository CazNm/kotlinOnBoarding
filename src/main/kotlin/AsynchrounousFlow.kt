import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.CacheRequest
import kotlin.concurrent.thread

fun main() {
//    representingMultipleValues()
//    runWithSuspendSimple()
//    flowExample()
//    runFlowIsCold()
//    flowCancellationBasic()
    flowBuilders()
}





fun representingMultipleValues() {
    simple().forEach { value -> println(value) }

    simpleWithSequence().forEach { value -> println(value) }
}

fun simple() : List<Int> = listOf(1,2,3)

fun simpleWithSequence() : Sequence<Int> = sequence {
    for(i in 1..3) {
        Thread.sleep(1000)
        yield(i)
    }
}

//suspending functions
fun runWithSuspendSimple() = runBlocking<Unit> {
    simpleSuspend().forEach { value -> println(value) }
}

suspend fun simpleSuspend() : List<Int> {
    delay(2000) // pretend we are doing something asyncronous here
    return listOf(1,2,3)
}



//flows

fun simpleFlow() : Flow<Int> = flow {
    for(i in 1..3) {
        delay(500)
        emit(i)
    }
}

fun flowExample() = runBlocking<Unit> {
    launch {
        for(k in 1..3) {
            println("I'm not blocked $k")
            delay(500)
        }

    }

    simpleFlow().collect() {value -> println(value) }
}


//flows are cold

fun coldSimple() : Flow<Int> = flow {
    println("flow started")
    for(i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun runFlowIsCold() = runBlocking<Unit> {
    println("Calling cold simple function...")
    val flow = coldSimple()
    println("Calling collect ...")
    flow.collect { value -> println(value) }
    println("Calling collect again ...")
    flow.collect { value -> println(value) }
}
// coldSimple 이 suspend 로 되어 있지 않기 때문에, coldSimple 이 call 되면 아무것도 기다리지 않고 결과값을 바로 return 한다.
// 그렇기 때문에 flow.collect 할 때 마다 flow 가 시작되어서 log 에 2번씩 찍히는 것.

//Flow cancellation Basic

fun simpleCancellation() : Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        println("Emitting $i")
        emit(i)
    }
}

fun flowCancellationBasic() = runBlocking {

    withTimeoutOrNull(250) {
        simpleCancellation().collect() { value -> println(value) }
    }
    println("Done")
}

//flow builders

fun flowBuilders() = runBlocking {
    (1..3).asFlow().collect { value -> println(value) }
}


//Intermediate flow operators

suspend fun performRequest(request: Int) : String {
    delay(1000)
    return "response $request"
}

fun intermediateFlow()  = runBlocking {
    (1..3).asFlow()
        .map { request -> performRequest(request)}
        .collect { response -> println(response) }
}