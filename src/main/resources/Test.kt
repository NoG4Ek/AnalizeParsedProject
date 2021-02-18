import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        Assert.assertEquals("in.blogspot.kmvignesh.calculator", appContext.packageName)
    }
}

open class T(n: Int) {
    open fun t() {
        open class A() {
            var t = 0
            fun g () {
                t++
                t--
            }
        }
        class Q() : A() {

        }
    }
}

interface I {}
interface II {}
class III(n: Int) : I, II, T(n) {}

class TT(n: Int) : T(n) {
    override fun t(){

    }

}

class g(n: Int) {
    private var isd = 0
    private val dqw = "ывыа"

    fun da() {
        val k = 0
        val j = 9
        fun net() {
            val i = 9
        }
    }

    fun f () {
        when (isd) {
            0 ->
            {
                if (isd == 0) {
                    isd++
                    da()
                }
            }
        }
    }
}
