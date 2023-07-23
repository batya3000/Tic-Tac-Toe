package com.android.batya.tictactoe.presentation.di

import com.android.batya.tictactoe.util.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val firebaseModule = module {

    single { Firebase.auth }
    single { Firebase.database }

    single(named(Constants.USERS_REF)){ (get<FirebaseDatabase>()).getReference(Constants.USERS_REF)}

    single(named(Constants.ROOMS_REF)){ (get<FirebaseDatabase>()).getReference(Constants.ROOMS_REF)}
    single(named(Constants.ROOMS_WAITING_REF)){ (get<FirebaseDatabase>()).getReference(Constants.ROOMS_REF).child(Constants.ROOMS_WAITING_REF)}
    single(named(Constants.ROOMS_RUNNING_REF)){ (get<FirebaseDatabase>()).getReference(Constants.ROOMS_REF).child(Constants.ROOMS_RUNNING_REF)}

    single(named(Constants.INVITATIONS_REF)){ (get<FirebaseDatabase>()).getReference(Constants.INVITATIONS_REF)}
    single(named(Constants.FRIEND_INVITATIONS_REF)){ (get<FirebaseDatabase>()).getReference(Constants.INVITATIONS_REF).child(Constants.FRIEND_INVITATIONS_REF)}
    single(named(Constants.BATTLE_INVITATIONS_REF)){ (get<FirebaseDatabase>()).getReference(Constants.INVITATIONS_REF).child(Constants.BATTLE_INVITATIONS_REF)}


}

