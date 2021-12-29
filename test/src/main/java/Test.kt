open class A {
}

class B : A() {

}


fun main() {

    val a : Class<*> = A::class.java
    val b : Class<*> = B::class.java

    println(a::class.java.isAssignableFrom(b))

}