import { useState, useEffect, useCallback } from 'react'
import './App.css'
import axios from 'axios'
import {useDropzone} from 'react-dropzone'


const UserProfiles = () => {

  const [userProfiles, setUserProfiles] = useState([]);
  //const [refresh, setRefresh] = useState({});

  const fetchUserProfiles = () => {
    axios.get("http://localhost:8080/api/v2/user-profile").then(res => {
      console.log(res);
      setUserProfiles(res.data)
    })
  }

  useEffect(() => {
    fetchUserProfiles();
  }, []);

  return userProfiles.map((userProfile, index) => {
      return (
      <div key={index}>
        {userProfile.userProfileId ?
            <img
                src={`http://localhost:8080/api/v2/user-profile/${userProfile.userProfileId}/image/download`}
                alt="image"
            /> : null}
        <br/>
        <br/>
        <h1>{userProfile.username}</h1>
        <p>{userProfile.userProfileId}</p>
        <MyDropzone userProfileId={userProfile.userProfileId}
          //onUploaded={() => setRefresh(prev => ({...prev, [userProfile.userProfileIdAsString]: Date.now()}))}
        />
        <br/>
      </div>
    )
  }) 
}

function MyDropzone({ userProfileId, onUploaded }) {
  const onDrop = useCallback(acceptedFiles => {
    const file = acceptedFiles[0];

    console.log(file);

    const formData = new FormData();
    formData.append("file", file);
    
    
    axios.post(`http://localhost:8080/api/v2/user-profile/${userProfileId}/image/upload`, formData,
      {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      }
    ).then(() => {
      console.log("file uploaded successfully");
      if (typeof onUploaded === 'function') {
        onUploaded();
      }
    }).catch(err => {
      console.log(err);
    });
  }, []);
  const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

  return (
    <div {...getRootProps()}>
      <input {...getInputProps()} />
      {
        isDragActive ?
          <p>Drop the image here ...</p> :
          <p>Drag 'n' drop profile image, or click to select profile image</p>
      }
    </div>
  )
}

function App() {

  return (
      <div className="App">
        <UserProfiles />
      </div>
  )
}

export default App
