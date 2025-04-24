package com.example.home_switch

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FirebaseManager {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var courseList: ArrayList<RoomModel>
    private lateinit var keyList: List<String>
    private lateinit var listener: FirebaseListener
    private var user = FirebaseAuth.getInstance().currentUser
    private var uid = user!!.uid

    constructor(fbListener: FirebaseListener) {
        setListener(fbListener)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
        courseList = ArrayList()
        keyList = ArrayList()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                courseList.clear()
                (keyList as ArrayList<String>).clear()
                keyList.size
                for (dataSnapshot in snapshot.children) {
                    val model = dataSnapshot.getValue(
                        RoomModel::class.java
                    )
                    val key = dataSnapshot.key
                    key?.let { (keyList as ArrayList<String>).add(it) }
                    if (model != null) {
                        courseList.add(model)
                    }
                }

                listener.OnDataChange(courseList)
                setkeylist(keyList as ArrayList<String>)

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun setkeylist(keyList: ArrayList<String>) {
        this.keyList = keyList
    }

    fun getDatabaseReference(): DatabaseReference {
        return databaseReference
    }

    fun setListener(fbListener: FirebaseListener) {
        listener = fbListener
    }

    companion object {
        var _this: FirebaseManager? = null
        fun getFirebaseManager(fbListener: FirebaseListener): FirebaseManager {
            //if (_this == null)
            _this = FirebaseManager(fbListener)
            return _this as FirebaseManager
        }
    }

    fun setCurrentDatabase() {
        user = FirebaseAuth.getInstance().currentUser
        uid = user!!.uid
        if (databaseReference != null) {
            courseList = ArrayList()
            keyList = ArrayList()
            databaseReference = firebaseDatabase.getReference(uid).child("HomeSwitch")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    courseList.clear()
                    (keyList as ArrayList<String>).clear()
                    keyList.size
                    for (dataSnapshot in snapshot.children) {
                        val model = dataSnapshot.getValue(
                            RoomModel::class.java
                        )
                        val key = dataSnapshot.key
                        key?.let { (keyList as ArrayList<String>).add(it) }
                        if (model != null) {
                            courseList.add(model)
                        }
                    }
                    listener.OnDataChange(courseList)
                    setkeylist(keyList as ArrayList<String>)
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        }
    }

    fun getDataList(): ArrayList<RoomModel> {
        return courseList
    }

    fun getkeyList(): ArrayList<String> {
        return keyList as ArrayList<String>
    }

}