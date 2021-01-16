package com.example.mytimetablemaker

//時刻表タイトルを取得
fun String.timeTableTitle(arrivestation: String) =
        "($this ${R.string.koron.strings} $arrivestation${R.string.houmen.strings})"

//内部ストレージに保存された各時間の時刻表データをStringとして取得
fun String.timeTableKey(i: Int, hour: Int, currentday: Int): String =
        "${this}timetable${i + 1}hour${hour.addZeroTime}${currentday.weekDayOrEnd}"

//内部ストレージに保存された各時間の時刻表データをStringとして取得
fun String.timeTableString(i: Int, hour: Int, currentday: Int): String =
        timeTableKey(i, hour, currentday).savedText("").timeSorting.timeString

//内部ストレージに保存された各時間の時刻表データをInt配列として取得
fun String.timeTableArrayInt(i: Int, hour: Int, currentday: Int): Array<Int> =
        "${this}timetable${i + 1}hour${hour.addZeroTime}${currentday.weekDayOrEnd}".savedText("")
                .timeSorting.timeArrayInt(hour)

//
val Int.weekDayOrEnd get() = when(this) { 0, 6 -> "weekend" else -> "weekday" }

//
fun Int.dayOrEndString(weekendstring: String, weekdaystring: String): String = when (this) {
    0, 6 -> weekendstring
    else -> weekdaystring
}

//
fun Int.dayOrEndColor(weekendcolor: Int, weekdaycolor: Int): Int = when (this) {
    0, 6 -> weekendcolor
    else -> weekdaycolor
}

//
val Int.otherDay: Int get() = when (this) { in 1..5 -> 0 else -> 1 }

//
fun String.timeArrayInt(hour: Int): Array<Int> = if (this != "") { this
        .split(" ")
        .asSequence()
        .distinct()
        .takeWhile { it != "" }
        .map { it.toInt() }
        .takeWhile { it < 60 }
        .map { it + hour * 100 }
        .toList()
        .toTypedArray()
        .sortedArray()
} else {
    arrayOf()
}

//
val String.timeString: String get() = if (this != "") { this
        .split(" ")
        .distinct()
        .takeWhile { it != "" }
        .map{ it.toInt() }
        .takeWhile { it < 60 }
        .toTypedArray()
        .sortedArray()
        .joinToString(" ") { it.toString() }
} else {
    ""
}

//時刻表の時刻の追加
fun String.addTime(time: String): String = "$this $time"
        .removePrefix(" ")
        .split(" ")
        .asSequence()
        .distinct()
        .takeWhile { it != "" }
        .map { it.toInt() }
        .takeWhile { it < 60 }
        .sorted()
        .joinToString(" ") { it.toString() }

//時刻表の時刻の削除
fun String.deleteTime(time: String): String = this
        .split(" ")
        .filter { it != time }
        .joinToString(" ") { it }

//時刻データを整理
val String.timeSorting: String get() = this
        .replace("+","")
        .replace("-","")
        .replace("*","")
        .replace("/","")
        .replace("(","")
        .replace(")","")
        .replace(",","")
        .replace(".","")
        .replace(";","")
        .replace("N","")
        .replace("#","")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removePrefix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .removeSuffix(" ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
        .replace("  "," ")
