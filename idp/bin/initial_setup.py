# The contents of this file are subject to the Mozilla Public License
# Version 2.0 (the "License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
#    http://www.mozilla.org/MPL/
#
# Software distributed under the License is distributed on an "AS IS" basis,
# WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
# for the specific language governing rights and limitations under the
# License.
#
# Copyright 2015 Magenta Aps
#
import os
import urllib2
import zipfile
import tempfile
import time
import platform
import subprocess
import ssl
import shutil

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BIN_DIR = os.path.join(BASE_DIR, "bin")


def download(url):
    file_name = url.split('/')[-1]
    file_name = os.path.join(BASE_DIR, file_name);

    if os.path.exists(file_name):
        print "Using existing version of %s for %s" % (file_name, url)
        print "Delete it if you wish to re-download"
        return file_name

    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE

    u = urllib2.urlopen(url, context=ctx)
    f = open(file_name, 'wb')
    meta = u.info()
    file_size = int(meta.getheaders("Content-Length")[0])
    print "Downloading: %s Bytes: %s" % (file_name, file_size)

    file_size_dl = 0
    block_sz = 8192
    while True:
        buffer = u.read(block_sz)
        if not buffer:
            print ""
            break

        file_size_dl += len(buffer)
        f.write(buffer)
        status = r"%10d  [%3.2f%%]" % (
            file_size_dl,
            file_size_dl * 100. / file_size
        )
        status = status + chr(8)*(len(status)+1)
        print status,

    f.close()
    return file_name


def unpack_wso2():

    ROOT_DIR = os.path.dirname(os.path.dirname(__file__))
    DEST_DIR = os.path.join(ROOT_DIR, "wso2")

    if os.path.exists(os.path.join(DEST_DIR, "bin", "version.txt")):
        print "WSO2 seems to already exists, skipping unpacking WSO2"
        return True

    OUTPUT_DIR = os.path.join(ROOT_DIR, "tmp_unzip")
    ZIP_FILE = os.path.join(ROOT_DIR, "wso2.zip")

    if not os.path.exists(ZIP_FILE):
        print "".join([
            "No wso2.zip file found. ",
            "Please download wso2 5.3.0 as a zip file, ",
            "place it in the same folder as the initial_setup.bat file and ",
            "rename it to wso2.zip"
        ])
        return False

    EXCLUDE_LIST_FILE = os.path.join(ROOT_DIR, "unzip_exclude_list.txt")
    exclude_list = []
    f = open(EXCLUDE_LIST_FILE, 'r')
    for line in f:
        # Remove wso2/ at start and whitespaces at end
        line = line.strip()[5:]
        if line:
            exclude_list.append(line)
    f.close()

    zip_archive = zipfile.ZipFile(ZIP_FILE, 'r', allowZip64=True)

    members = zip_archive.namelist()
    first_name = members[0]
    archive_dir = first_name[:first_name.index("/") + 1]
    exclude_set = set([archive_dir + x for x in exclude_list])
    members = [x for x in members if x not in exclude_set]

    print "Unpacking wso2.zip"
    zip_archive.extractall(path=OUTPUT_DIR, members=members)

    print "Moving unpacked files to wso2 folder"
    UNZIPPED_ROOT = os.path.join(OUTPUT_DIR, archive_dir[:-1])

    for path, dirs, files in os.walk(UNZIPPED_ROOT):
        sub_path = path[len(UNZIPPED_ROOT) + 1:]
        for x in dirs:
            dest = os.path.join(DEST_DIR, sub_path, x)
            if not os.path.exists(dest):
                os.mkdir(dest)
        for x in files:
            dest = os.path.join(DEST_DIR, sub_path, x)

            if os.path.exists(dest):
                os.remove(dest)

            os.rename(os.path.join(path, x), dest)

    shutil.rmtree(OUTPUT_DIR)

    return True


if __name__ == '__main__':

    if not unpack_wso2():
        print "Unpacking of WSO2 failed!"
        exit(1)

    print "initial_setup.py done"
