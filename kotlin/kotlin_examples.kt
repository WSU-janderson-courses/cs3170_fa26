/**
 * CS 3170
 * Kotlin examples
 */
 
fun main() {
    // print/println
	println("Hello world!")
    println("Next line")
    
    // val variableName: TypeName = value
    val numDogs: Int = 0
    
    val numCats: Int = 2
    
    
    // val must be initialized/set when created
    val totalPets = numDogs + numCats
    
    // strings    
    // string concatenation
    val petString: String = "There are " + numDogs + " dogs and " + numCats + " cats"
    
    println(petString)
    
    // string length
    val strLen = petString.length
    
    println("petString has " + strLen + " characters")
    
    // string template {}
    println("There are ${totalPets} pets in total")
    println("petString has ${petString.length} characters")
    
    // val vs var
    // val must be assigned when created and cannot be changed
    val height: Int = 12 // if the type of the RHS is known (Int) you can omit the type
    
    // height = 13 // uncomment to see the error

    println(height)
    
    // var can be declared and initialized later, but the type must be specified
    var width: Double
    
    width = 5.8

    println(width)

    width = 5.9
    
    println(width)
    
    // if/else/else if
    if (numDogs > numCats) {
        println("more dogs than cats")
    } else if (numDogs == numCats) {
        println("same number of cats and dogs")
    } else {
        println("more cats than dogs")
    }
    
    // ranges  
    // if in range
    if (numDogs in 1..5){
        println("there are between 1 and 5 dogs")
    } else if (numDogs in 6..10) {
        println("there are between 6 and 10 dogs")
    } else {
        println("there are more than 10 dogs")
    }
    
    // when()
    // v1 -> print...
    // in range -> print...
    // else -> print...
    when (numDogs) {
        0 -> println("there are no dogs")
        in 1..5 -> println("there are between 1 and 5 dogs")
        in 6..10 -> println("there are between 6 and 10 dogs")
        else -> println("there are more than 10 dogs")
    }
    
    val dogString = when (numDogs) {
        0 -> "there are no dogs"
        in 1..5 -> "there are between 1 and 5 dogs"        
        in 6..10 -> "there are between 6 and 10 dogs"
        else -> "there are more than 10 dogs"
    }
    println(dogString)
    
    // arrayOf()
    val animals = arrayOf("dogs", "cats", "bunnies")
    // for(element in array)
    for (animal in animals) {
        println(animal)
    }
    
    // for((index, element) in array.withIndex() )
    for ((index, animal) in animals.withIndex()) {
        println("animal number $index: $animal")
    }
    
    // for( i in range), downTo, step, 'a'..'z'
    for (i in 1..5) {
        println(i)
    }
    
    // while()
    // do / while()
    
    // Immutable listOf()
    val instruments = listOf("guitar", "bass", "drums", "trombone")
    println(instruments)
    
    if (instruments.contains("bass")) {
        println("bass is in the instruments list")
    } else {
        println("bass is not in the instruments list")
    }
    
    // instruments.add(5, "cowbell") // we can't add to the list nor modify elements
    
    
    // mutableListOf()
    val genres = mutableListOf("rock", "classical", "hip hop", "jazz")
    println(genres)
    
    // remove an element
    genres.remove("classical")
    println("removed \"classical\" $genres")
    
    // add an element
    genres.add("R&B")
    println("added \"R&B\" $genres")
    
    // combining lists
    val moreInstruments = listOf("piano", "cello", "violin")
    val allInstruments = moreInstruments + instruments
    println(allInstruments)
    
    // null safety
    // null not allowed by default
    // val numBooks: Int = null // just 'Int' can't be null
    var numBooks: Int? = null // Int? allows numBooks to be null
    // null check
    if (numBooks != null) {
        numBooks = numBooks.dec() // decrement numBooks if it isn't null
    } else {
        println("numBooks is null")
    }
    
    // safe call operator (?)
    // instead of checking for null, we can use the safe call operator
    numBooks = numBooks?.dec() // decrement numBooks if it isn't null, otherwise don't 
    println(numBooks)
    
    numBooks = 6 // now numBooks isn't null
    numBooks = numBooks?.dec()
    println(numBooks)
    
    // not-so-safe call operator (!!)
    // if we're certain variable is not null, we can be not-so-safe and call the method 
    numBooks = numBooks!!.dec()
    println(numBooks)

    numBooks = null
    // numBooks = numBooks!!.dec() // this will crash the program

    // Elvis operator (?:)
    // we can safely decrement or assign a value depending on if numBooks is null
    numBooks = numBooks?.dec() ?: 0
    println(numBooks)
}