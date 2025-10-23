import { useState, useEffect, useCallback } from 'react'
import './App.css'
import axios from 'axios'
import {useDropzone} from 'react-dropzone'


const Images = () => {

  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  //const [refresh, setRefresh] = useState({});

  const fetchImages = () => {
    setLoading(true);
    setError(null);
    axios.get("http://localhost:8080/api/v1/image-gallery").then(res => {
      console.log(res);
      setImages(res.data || []);
      setLoading(false);
    }).catch(err => {
      console.error("Error fetching images: ", err);
      setError("Failed to load images. Please check if backend is running");
      setImages([]);
      setLoading(false);
    })
  }

  useEffect(() => {
    fetchImages();
  }, []);

  if(loading) {
    return <div>
      Loading images...
    </div>;
  }

  if(error) {
    return (
      <div>
        <p style = {{color:"red"}}>{error}</p>
        <button onClick={fetchImages}> Retry </button>
      </div>
    );
  }

  if(images.length === 0) {
    return (
      <div>
        <h2>No images yet!</h2>
        <p>Upload your first image to get started.</p>
        <MyDropzone onUploaded={fetchImages}/>
      </div>
    );
  }

  return (
  <>
    {images.map((image, index) => (
      <div key={index}>
        {image.imageKey ? 
          <img
            src={`http://localhost:8080/api/v1/image-gallery/${image.imageKey}/image/download`}
            alt={image.title || "image"}
          />
         : null}
         <br/>
         <br/>
         <h1>{image.title || "untitled"}</h1>
         <p>{image.description || "no description"}</p>
         <MyDropzone imageKey={image.imageKey} onUploaded={fetchImages} />
         <br/>
      </div>
    ))}
  </>
);
  
}

function MyDropzone({imageKey, onUploaded}) {

  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState(null);

  const onDrop = useCallback(acceptedFiles => {
    const file = acceptedFiles[0];
    if(!file) return;

    console.log(file);
    setUploading(true);
    setUploadError(null);

    const formData = new FormData();
    formData.append("file", file);
    
    // const uploadUrl = imageKey
    //   ? `http://localhost:8080/api/v1/image-gallery/${imageKey}/image/upload`
    //   : `http://localhost:8080/api/v1/image-gallery/image/upload`

    axios.post(`http://localhost:8080/api/v1/image-gallery/image/upload`, formData,
      {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      }
    ).then(() => {
      setUploading(false);
      console.log("file uploaded successfully");
      if (typeof onUploaded === 'function') {   //if  you pass in an onUploaded function - can be used to refresh the page
        onUploaded();
      }
    }).catch(err => {
      console.log(err);
      setUploadError("Upload failed. Try again");
      setUploading(false);
    });
  }, [imageKey, onUploaded]);
  const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})


  return (
  <div {...getRootProps()} style={{
    border: '2px dashed #ccc',
    borderRadius: '4px',
    padding: '20px',
    textAlign: 'center',
    cursor: 'pointer',
    backgroundColor: isDragActive ? 'red' : 'green'
  }}>
    <input {...getInputProps()} />
    {
      isDragActive ?
        <p>Drop the image here ...</p> :
        <p>Drag 'n' drop profile image, or click to select profile image</p>
    }
  </div>
  )} 

function App() {

  return (
      <div className="App">
        <Images />
      </div>
  )
}

export default App
