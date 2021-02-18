@file:JvmName("InfoConst")

const val CLASS_DECLARATION = "classDeclaration"
const val FUNC_SUFFIX = "callSuffix"
val REG_ASSIGNMENT_UNARY = Regex(".*(ASSIGNMENT).*|(DECR)|(INCR)")
val REG_IF_WHEN_EXPRESSIONS = Regex("ifExpression|whenExpression")
val REG_OVERRIDE = Regex(".*(OVERRIDE).*")