package com.maricoolsapps.e_commerce.data.interfaces
interface ConstraintInstructions{
data class ConnectConstraint(val startID: Int, val startSide: Int, val endID: Int, val endSide: Int) : ConstraintInstructions
data class DisconnectConstraint(val startID: Int, val startSide: Int) : ConstraintInstructions
}