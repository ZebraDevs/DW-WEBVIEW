package com.zebra.remotedisplayservice;
// Declare any non-default types here with import statements interface
interface IRemoteDisplayService {
        /**
         * Have the service perform authentication of caller.
         * @return Returns true indicating authentication success and false
         otherwise.
        */
         boolean authenticate();
        /**
         * Have the service enable/disable displaying of pop up on remote device
         access.
         * @param mode true(disable pop up), false(enable pop up).
        */
         void suppressPopup(boolean mode);
        /**
         * Have the service close the current session.
         * @return Returns true indicating session is closed and false otherwise.
        */
         boolean close();

 }
