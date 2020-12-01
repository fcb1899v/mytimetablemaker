package com.example.mytimetablemaker

import android.widget.Toast
import com.example.mytimetablemaker.Application.Companion.context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.ktx.Firebase

class FirebaseFirestore {

    private val mainview = MainView()
    private val timetable = Timetable()

//    fun getLineInfoFirestore(goorback: String) {
//        val userid: String = Firebase.auth.currentUser!!.uid
//        val lineinfo: HashMap<String, String> = hashMapOf()
//        FirebaseFirestore.getInstance().collection("users").document(userid)
//            .collection("goorback").document(goorback).get()
//            .addOnSuccessListener { result ->
//                forEach (document in result) {
//                    lineinfo[document.id] = document.data
//                }
//                Toast.makeText(context, R.string.successed_to_get_data, Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { Toast.makeText(context, R.string.failed_to_get_data, Toast.LENGTH_SHORT).show() }
//    }

    fun saveFirestore() {
        val goorbackarray: Array<String> = arrayOf("back1", "go1", "back2", "go2")
        val batch = FirebaseFirestore.getInstance().batch()
        (0..3).forEach { i: Int ->
            (1..3).forEach { j: Int ->
                (0..1).forEach { k: Int ->
                    saveTimetableFirestore(goorbackarray[i], j, k, batch)
                }
            }
            saveLineInfoFirestore(goorbackarray[i],batch)
        }
        batch.commit()
            .addOnSuccessListener { Toast.makeText(context, R.string.successed_to_save_data, Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(context, R.string.failed_to_save_data, Toast.LENGTH_SHORT).show() }

    }

    private fun saveLineInfoFirestore(goorback: String, batch: WriteBatch){
        val userid: String = Firebase.auth.currentUser!!.uid
        val lineinfo = lineInfoHushMap(goorback)
        val ref = FirebaseFirestore.getInstance().collection("users").document(userid)
            .collection("goorback").document(goorback)
        batch.set(ref, lineinfo)
    }

    private fun saveTimetableFirestore(goorback: String, linenumber: Int, day: Int, batch: WriteBatch){
        val userid: String = Firebase.auth.currentUser!!.uid
        val timetablearray: Array<String> = timetable.getTimetableStringArray(goorback, linenumber, day)
        val timetable: HashMap<String, String> = timetableHushMap(timetablearray)
        val endorday: String = when(day) { 0 -> "weekend" else -> "weekday" }
        val ref = FirebaseFirestore.getInstance().collection("users").document(userid)
            .collection("goorback").document(goorback)
            .collection("timetable").document("timetable${linenumber}${endorday}")
        batch.set(ref, timetable)
    }

    private fun lineInfoHushMap(goorback: String): HashMap<String, String>{
        val changeline: String = goorback.changeLine.toString()
        val departpoint: String = goorback.departPoint(R.string.office.strings, R.string.home.strings)
        val arrivalpoint: String = goorback.arrivePoint(R.string.office.strings, R.string.home.strings)
        val departstation: Array<String> = mainview.getDepartStation(goorback, changeline.toInt())
        val arrivalstation: Array<String> = mainview.getArriveStation(goorback, changeline.toInt())
        val linename: Array<String> = mainview.getLineName(goorback, changeline.toInt())
        val linecolor: Array<String> = mainview.getLineColor(goorback, changeline.toInt())
        val ridetime: Array<String> = mainview.getRideTime(goorback, changeline.toInt())
        val transportation: Array<String> = mainview.getTransportation(goorback, changeline.toInt())
        val transittime: Array<String> = mainview.getTransitTime(goorback, changeline.toInt())
        return hashMapOf(
            "changeline" to changeline,
            "departpoint" to departpoint,
            "arrivalpoint" to arrivalpoint,
            "departstation1" to departstation[0],
            "departstation2" to departstation[1],
            "departstation3" to departstation[2],
            "arrivalstation1" to arrivalstation[0],
            "arrivalstation2" to arrivalstation[1],
            "arrivalstation3" to arrivalstation[2],
            "linename1" to linename[0],
            "linename2" to linename[1],
            "linename3" to linename[2],
            "linecolor1" to linecolor[0],
            "linecolor2" to linecolor[1],
            "linecolor3" to linecolor[2],
            "ridetime1" to ridetime[0],
            "ridetime2" to ridetime[1],
            "ridetime3" to ridetime[2],
            "transportatione" to transportation[0],
            "transportation1" to transportation[1],
            "transportation2" to transportation[2],
            "transportation3" to transportation[3],
            "transittimee" to transittime[0],
            "transittime1" to transittime[1],
            "transittime2" to transittime[2],
            "transittime3" to transittime[3]
        )
    }

    private fun timetableHushMap(timetablearray: Array<String>): HashMap<String, String> {
        return hashMapOf(
            "hour04" to timetablearray[0],
            "hour05" to timetablearray[1],
            "hour06" to timetablearray[2],
            "hour07" to timetablearray[3],
            "hour08" to timetablearray[4],
            "hour09" to timetablearray[5],
            "hour10" to timetablearray[6],
            "hour11" to timetablearray[7],
            "hour12" to timetablearray[8],
            "hour13" to timetablearray[9],
            "hour14" to timetablearray[10],
            "hour15" to timetablearray[11],
            "hour16" to timetablearray[12],
            "hour17" to timetablearray[13],
            "hour18" to timetablearray[14],
            "hour19" to timetablearray[15],
            "hour20" to timetablearray[16],
            "hour21" to timetablearray[17],
            "hour22" to timetablearray[18],
            "hour23" to timetablearray[19],
            "hour24" to timetablearray[20],
            "hour25" to timetablearray[21]
        )
    }
}

