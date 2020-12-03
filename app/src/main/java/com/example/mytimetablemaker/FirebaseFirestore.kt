package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.example.mytimetablemaker.Application.Companion.context
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.ktx.Firebase

class FirebaseFirestore {

    private val mainview = MainView()
    private val timetable = Timetable()
    private val setting = Setting()

    private val goorbackarray: Array<String> = arrayOf("back1", "go1", "back2", "go2")

    fun getAlertFirestore(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.overwirrten_byserverdata.strings)
            setNegativeButton(R.string.yes) { _, _ -> getFirestore() }
            setPositiveButton(R.string.no, null)
            show()
        }
    }

    fun saveAlertFirestore(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.overwirrten_serverdata.strings)
            setNegativeButton(R.string.yes) { _, _ -> saveFirestore() }
            setPositiveButton(R.string.no, null)
            show()
        }
    }

    fun getFirestore() {
        (0..3).forEach { i: Int ->
            (0..2).forEach { linenumber: Int ->
                (0..1).forEach { day: Int ->
                    (4..25).forEach { hour: Int ->
                        getTimetableFireStore(goorbackarray[i], linenumber, day, hour)
                    }
                }
            }
            getLineInfoFirestore(goorbackarray[i])
        }
    }

    private fun saveFirestore() {
        val batch = FirebaseFirestore.getInstance().batch()
        (0..3).forEach { i: Int ->
            (0..2).forEach { linenumber: Int ->
                (0..1).forEach { day: Int ->
                    saveTimetableFirestore(goorbackarray[i], linenumber, day, batch)
                }
            }
            saveLineInfoFirestore(goorbackarray[i], batch)
        }
        batch.commit()
            .addOnSuccessListener { Toast.makeText(context, R.string.successed_to_save_data, Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(context, R.string.failed_to_save_data, Toast.LENGTH_SHORT).show() }
    }

    private fun getLineInfoFirestore(goorback: String) {
        val userid: String = Firebase.auth.currentUser!!.uid
        val userdb =  FirebaseFirestore.getInstance().collection("users").document(userid)
        val ref = userdb.collection("goorback").document(goorback)
        ref.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    saveLineInfoToPref(goorback, task)
                    Toast.makeText(context, R.string.successed_to_get_data, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, R.string.failed_to_get_data, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getTimetableFireStore(goorback: String, linenumber: Int, day: Int, hour: Int) {
        val userid: String = Firebase.auth.currentUser!!.uid
        val userdb =  FirebaseFirestore.getInstance().collection("users").document(userid)
        val ref = userdb.collection("goorback").document(goorback)
        ref.collection("timetable").document("timetable${linenumber + 1}${day.weekDayOrEnd}").get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val key  ="${goorback}timetable${linenumber + 1}hour${hour.addZeroTime}${day.weekDayOrEnd}"
                    setting.prefSaveText(context, key, task.result?.get("hour${hour.addZeroTime}").toString().stringIfNullOrNot)
                }
            }
    }

    private fun saveLineInfoToPref(goorback: String, task: Task<DocumentSnapshot>) {
        setting.prefSaveBoolean(context, "${goorback}switch", task.result?.get("switch").toString().toBoolean().booleanIfNullOrNot)
        setting.prefSaveText(context, "${goorback}changeline", task.result?.get("changeline").toString().stringIfNullOrNot)
        setting.prefSaveText(context, goorback.goOrBackString("destination", "departurepoint"), task.result?.get("departpoint").toString().stringIfNullOrNot)
        setting.prefSaveText(context, goorback.goOrBackString("departurepoint", "destination"), task.result?.get("arrivalpoint").toString().stringIfNullOrNot)
        (1..3).forEach { i: Int ->
            setting.prefSaveText(context, "${goorback}departstation${i}", task.result?.get("departstation${i}").toString().stringIfNullOrNot)
            setting.prefSaveText(context, "${goorback}arrivalstation${i}", task.result?.get("arrivalstation${i}").toString().stringIfNullOrNot)
            setting.prefSaveText(context, "${goorback}linename${i}", task.result?.get("linename${i}").toString().stringIfNullOrNot)
            setting.prefSaveText(context, "${goorback}linecolor${i}", task.result?.get("linecolor${i}").toString().stringIfNullOrNot)
            setting.prefSaveText(context, "${goorback}ridetime${i}", task.result?.get("ridetime${i}").toString().stringIfNullOrNot)
            setting.prefSaveText(context, "${goorback}transportation${i}", task.result?.get("transportation${i}").toString().stringIfNullOrNot)
            setting.prefSaveText(context, "${goorback}transittime${i}", task.result?.get("transittime${i}").toString().stringIfNullOrNot)
        }
        setting.prefSaveText(context, "${goorback}transportatione", task.result?.get("transportatione").toString().stringIfNullOrNot)
        setting.prefSaveText(context, "${goorback}transittimee", task.result?.get("transittimee").toString().stringIfNullOrNot)
    }

    private fun saveLineInfoFirestore(goorback: String, batch: WriteBatch){
        val userid: String = Firebase.auth.currentUser!!.uid
        val userdb =  FirebaseFirestore.getInstance().collection("users").document(userid)
        val ref = userdb.collection("goorback").document(goorback)
        batch.set(ref, setLineInfo(goorback))
    }

    private fun saveTimetableFirestore(goorback: String, linenumber: Int, day: Int, batch: WriteBatch){
        val userid: String = Firebase.auth.currentUser!!.uid
        val userdb =  FirebaseFirestore.getInstance().collection("users").document(userid)
        val ref = userdb.collection("goorback").document(goorback)
        val timetablehour: TimetableHour = setTimetableHour(goorback, linenumber, day)
        val timetableref = ref.collection("timetable").document("timetable${linenumber + 1}${day.weekDayOrEnd}")
        batch.set(timetableref, timetablehour)
    }

    private fun setLineInfo(goorback: String): LineInfo{
        val switch: Boolean = mainview.getRoot2Boolean("${goorback}switch", false)
        val changeline: Int = goorback.changeLine
        val departpoint: String = goorback.departPoint(R.string.office.strings, R.string.home.strings)
        val arrivalpoint: String = goorback.arrivePoint(R.string.office.strings, R.string.home.strings)
        val departstation: Array<String> = mainview.getDepartStation(goorback, changeline)
        val arrivalstation: Array<String> = mainview.getArriveStation(goorback, changeline)
        val linename: Array<String> = mainview.getLineName(goorback, changeline)
        val linecolor: Array<String> = mainview.getLineColor(goorback, changeline)
        val ridetime: Array<String> = mainview.getRideTime(goorback, changeline)
        val transportation: Array<String> = mainview.getTransportation(goorback, changeline)
        val transittime: Array<String> = mainview.getTransitTime(goorback, changeline)
        return LineInfo(switch, changeline, departpoint, arrivalpoint,
            departstation[0], departstation[1], departstation[2],
            arrivalstation[0], arrivalstation[1], arrivalstation[2],
            linename[0], linename[1], linename[2],
            linecolor[0], linecolor[1], linecolor[2],
            ridetime[0], ridetime[1], ridetime[2],
            transportation[0], transportation[1], transportation[2], transportation[3],
            transittime[0], transittime[1], transittime[2], transittime[3]
        )
    }

    private fun setTimetableHour(goorback: String, linenumber: Int, day: Int): TimetableHour {
        val timetablearray: Array<String> = timetable.getTimetableStringArray(goorback, linenumber, day)
        return TimetableHour(timetablearray[0], timetablearray[1], timetablearray[2],
            timetablearray[3], timetablearray[4], timetablearray[5], timetablearray[6],
            timetablearray[7], timetablearray[8], timetablearray[9], timetablearray[10],
            timetablearray[11], timetablearray[12], timetablearray[13], timetablearray[14],
            timetablearray[15], timetablearray[16], timetablearray[17], timetablearray[18],
            timetablearray[19], timetablearray[20], timetablearray[21]
        )
    }

    fun resetPreferenceData() {
        (0..3).forEach { i: Int ->
            when(i) { 2, 3 -> setting.prefSaveBoolean(context, "${goorbackarray[i]}switch", false)}
            setting.prefSaveText(context, "${goorbackarray[i]}changeline", "")
            setting.prefSaveText(context, goorbackarray[i].goOrBackString("destination", "departurepoint"), "")
            setting.prefSaveText(context, goorbackarray[i].goOrBackString("departurepoint", "destination"), "")
            (1..3).forEach { j: Int ->
                setting.prefSaveText(context, "${goorbackarray[i]}departstation${j}", "")
                setting.prefSaveText(context, "${goorbackarray[i]}arrivalstation${j}", "")
                setting.prefSaveText(context, "${goorbackarray[i]}linename${j}", "")
                setting.prefSaveText(context, "${goorbackarray[i]}linecolor${j}", "")
                setting.prefSaveText(context, "${goorbackarray[i]}ridetime${j}", "")
                setting.prefSaveText(context, "${goorbackarray[i]}transportation${j}", "")
                setting.prefSaveText(context, "${goorbackarray[i]}transittime${j}", "")
            }
            setting.prefSaveText(context, "${goorbackarray[i]}transportatione", "")
            setting.prefSaveText(context, "${goorbackarray[i]}transittimee", "")
            (0..2).forEach { linenumber: Int ->
                (0..1).forEach { day: Int ->
                    (4..25).forEach { hour: Int ->
                        setting.prefSaveText(context, "${goorbackarray[i]}timetable${linenumber + 1}hour${hour.addZeroTime}${day.weekDayOrEnd}", "")
                    }
                }
            }
        }
    }

    data class LineInfo(
        val switch: Boolean = false,
        val changeline: Int = 0,
        val departpoint: String = "",
        val arrivalpoint: String = "",
        val departstation1: String = "",
        val departstation2: String = "",
        val departstation3: String = "",
        val arrivalstation1: String = "",
        val arrivalstation2: String = "",
        val arrivalstation3: String = "",
        val linename1: String = "",
        val linename2: String = "",
        val linename3: String = "",
        val linecolor1: String = "",
        val linecolor2: String = "",
        val linecolor3: String = "",
        val ridetime1:String = "",
        val ridetime2:String = "",
        val ridetime3:String = "",
        val transportatione: String = "",
        val transportation1: String = "",
        val transportation2: String = "",
        val transportation3: String = "",
        val transittimee:String = "",
        val transittime1:String = "",
        val transittime2:String = "",
        val transittime3:String = ""
    )

    data class TimetableHour(
        val hour04: String = "",
        val hour05: String = "",
        val hour06: String = "",
        val hour07: String = "",
        val hour08: String = "",
        val hour09: String = "",
        val hour10: String = "",
        val hour11: String = "",
        val hour12: String = "",
        val hour13: String = "",
        val hour14: String = "",
        val hour15: String = "",
        val hour16: String = "",
        val hour17: String = "",
        val hour18: String = "",
        val hour19: String = "",
        val hour20: String = "",
        val hour21: String = "",
        val hour22: String = "",
        val hour23: String = "",
        val hour24: String = "",
        val hour25: String = ""
    )
}

