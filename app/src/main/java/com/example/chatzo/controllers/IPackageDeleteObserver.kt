package com.example.chatzo.controllers

import android.os.*

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/fuzion24/Development/workspace/PackageManager/src/android/content/pm/IPackageDeleteObserver.aidl
 */ /**
 * API for deletion callbacks from the Package Manager.
 *
 * {@hide}
 */
interface IPackageDeleteObserver : IInterface {
    /** Local-side IPC implementation stub class.  */
    abstract class Stub : Binder(), IPackageDeleteObserver {
        override fun asBinder(): IBinder {
            return this
        }

        @Throws(RemoteException::class)
        public override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            when (code) {
                IBinder.INTERFACE_TRANSACTION -> {
                    reply!!.writeString(interfaceDescriptor)
                    return true
                }
                TRANSACTION_packageDeleted -> {
                    data.enforceInterface(interfaceDescriptor)
                    val _arg0: Boolean
                    _arg0 = 0 != data.readInt()
                    packageDeleted(_arg0)
                    return true
                }
            }
            return super.onTransact(code, data, reply, flags)
        }

        private class Proxy internal constructor(private val mRemote: IBinder) : IPackageDeleteObserver {
            override fun asBinder(): IBinder {
                return mRemote
            }

            @Throws(RemoteException::class)
            override fun packageDeleted(succeeded: Boolean) {
                val _data = Parcel.obtain()
                try {
                    _data.writeInterfaceToken(interfaceDescriptor)
                    _data.writeInt(if (succeeded) 1 else 0)
                    mRemote.transact(TRANSACTION_packageDeleted, _data, null, IBinder.FLAG_ONEWAY)
                } finally {
                    _data.recycle()
                }
            }

        }

        companion object {
            const val interfaceDescriptor = "IPackageDeleteObserver"

            /**
             * Cast an IBinder object into an IPackageDeleteObserver interface,
             * generating a proxy if needed.
             */
            fun asInterface(obj: IBinder?): IPackageDeleteObserver? {
                if (obj == null) {
                    return null
                }
                val iin = obj.queryLocalInterface(interfaceDescriptor)
                return if (iin != null && iin is IPackageDeleteObserver) {
                    iin
                } else Proxy(obj)
            }

            const val TRANSACTION_packageDeleted = IBinder.FIRST_CALL_TRANSACTION + 0
        }

        /** Construct the stub at attach it to the interface.  */
        init {
            attachInterface(this, interfaceDescriptor)
        }
    }

    @Throws(RemoteException::class)
    fun packageDeleted(succeeded: Boolean)
}