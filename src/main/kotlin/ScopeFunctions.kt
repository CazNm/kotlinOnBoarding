import kotlin.random.*

data class Person(var name : String, var age : Int, var city : String) {
    fun moveTo(newCity : String) {city = newCity}
    fun incrementAge() {age ++}
}

fun main() {
//    usageExampleScopeFunction()
//    sameCodeWithOutScopeFunction()
//    checkingItWithRandom()

    letExample()
    exampleWith()
}

//Kotlin standard library 에서는

fun usageExampleScopeFunction() {
    Person("Alice", 20, "Amsterdam").let {
        println(it)
        it.moveTo("London")
        it.incrementAge()
        println(it)
    }
}

fun sameCodeWithOutScopeFunction() {
    val alice = Person("Alice", 20 , "Amsterdam")
    println(alice)
    alice.moveTo("London")
    alice.incrementAge()
    println(alice)
}

//Scope Function 은 기술적인 향상점을 제공하지는 않지만 Code 의 간결함과 가독성을 향상시켜준다.

//Scope Function 은 비슷하기 때문에 차이점을 잘 알고 있어야 용도에 맞게 사용할 수 있다.
//크게 2가지 차이점을 가진다.
// 1. Context Object 를 참조하는 방식
// 2. return Value

// Scope function 은 2가지 방식으로 Context Object 에 접근 가능하다.
// this 와 it 중에 하나로..
// this 는 lambda receiver , it 은 lambda argument 이다. (??? lambda receiver, lambda argument 의 차이점은 뭘까?)

fun differenceOfLambdaReceiverAndArgument() {
    val str = "Hello"

    str.run {
        println("The string's length: $length")
        println("The string's length: ${this.length}") //does same
    }

    str.let {
        println("The strings' length: ${it.length}")
    }
}

// run , with, 그리고 apply 는 context object 를 lambda receiver 로써 this 키워드로 접근이 가능하다.
// 이 경우에는 ordinary class function 으로, this 를 통해 member 에 접근이 가능하다.
// this 키워드를 생략하고, code 를 간결하게 만들 수 있지만 this 의 member 와 외부 변수나 function 들과 혼동이 생길 수 있으니,
// this 의 사용이 권장된다.

fun accessLambdaReceiver() {
    val adam = Person("adam", 10, "Seoul").apply {
        age = 20
        city = "London" // same as this.city
    }

    println(adam)
}

// let 그리고 also 는 context object 를 lambda argumnet 로써 가진다. argument 가 명시되어 있지 않으면, default 값인 it 으로 접근이 가능하다.
// 그러나 object function 이나 properties 에 접근하기 위해 필요한 'this' 와 같은 명시적 object 가 존재하지 않는다.
// it 은 code 상에서 여러 변수들 사용할 때 유용하다.

fun writeToLog(message: String) {
    println("INFO: $message")
}

fun getRandomInt() : Int {
    return Random.nextInt(100).also {
        writeToLog("getRandomInt() generate value $it")
    }
}
// 아래와 같이 변수 이름을 따로 설정할 수 있다. 기본값 it

fun getRandomIntCustomName() : Int {
    return Random.nextInt(100).also { value ->
        writeToLog("getRandomIntCustomName() generate $value")
    }
}
fun checkingItWithRandom() {
    val i = getRandomInt()
    println(i)

    val c = getRandomIntCustomName()
    println(c)
}

// also apply with run let 의 사용법 그리고 예시


// apply (적용하다)
// 객체를 다른 변수에 할당하기 전에, property initialize/reallocate 명시 목적으로 사용한다.
// Lambda with Receiver : this 로 접근 . 객체 property 에 대한 재할당 권장 . 객체 혹은 객체 property 를 호출해도 무방
// return value( 전달 받은 receiver 반환 ) : 해당 객체를 변수에 할당하기 전 수행할 동작이 필요한 경우

fun applyExample() {
    val peter = Person("", 10, "").apply {
        name = "peter"
        age = 19
        city = "Seoul"
    }
}

// also (또한)
// 객체를 다른 변수에 할당하기 전에, "객체와 연관되지만 property 재할당 외의 작업"이 필요할 경우 사용
// Lambda argument : it 으로 접근 . 객체 혹은 객체 property 또는 객체를 아예 사용하지 않을 수도 있음을 암시 . 객체 property 에 대한 재할당 지양
// return value ( 전달 받은 receiver 반환 ) : 해당 객체를 할당하기 전 수행할 동작이 필요한 경우

fun alsoExample() {
    val peter = Person("peter", 19 , "Seoul").also { println() }
}


// run (수행하다)
// 해당 객쳉 대한 재사용이 자주되는 member 수정 및 호출 작업을 "함수로 정의" 할 때 사용한다.
// Lambda with Receiver : this 로 접근 . 객체 property 에 대한 재할당 권장 . 객체 혹은 객체 property 를 호출해도 무방
// return value ( lambda 반환 ) : receiver 반환 없이 로직 수행만을 원하는 경우

fun runExample(person: Person) = person.run {
    println(name)
    println(age)
    println(city)
}

// let (허용하다)
// Nullable 객체 또는 result of call chain 에 대해, "null 이 아닌 경우에만 " 실행해야할 객체를 호출하는 로직이 있을 경우 사용
// Lambda argument : it 으로 접근 . 객체 혹은 객체 property 또는 객체를 아예 사용하지 않을 수도 있음을 암시 . 객체 property 에 대한 재할당 지양
// return value ( lambda 반환 ) : receiver 반환 없이 로직 수행만을 원하는 경우


fun letExample() {
    val numbers = mutableListOf("one", "two", "three", "four", "five")
    numbers.map { it.length }.filter { it > 4 }.let { println(it) }
}

fun letNullableExample() {
    val str : String? = "Hello"

    val length = str?.let {
        println("let() called on $it")
        processNonNullString(it)
        it.length
    }

    println(length)
}

fun processNonNullString(str:String) {}


// with (~를 써서)
// 바로 접근 가능한 Non-Nullable 객체에 대한 멤버 수정 및 호출 작업이 필요한 경우.
// Lambda with Receiver : this 로 접근 . 객체 property 에 대한 재할당 권장 . 객체 혹은 객체 property 를 호출해도 무방
// return value ( lambda 반환 ) : receiver 반환 없이 로직 수행만을 원하는 경우

fun exampleWith() {
    val person  = Person("peter", 19, "Seoul")

    with(person) {
        city = "NewYork"
        println(name)
        println(age)
        println(city)

    }

    println(person)
}