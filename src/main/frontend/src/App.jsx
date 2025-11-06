import { useState, useEffect, useCallback } from 'react'
import './App.css'
import axios from 'axios'
import {useDropzone} from 'react-dropzone'

const Images = () => {

  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
  <div className='Images'>
    {images.map((image, index) => (
      <div key={index}>
        <h1 className='text'>{image.title}</h1>
        {image.imageKey ? 
          <img
            src={`http://localhost:8080/api/v1/image-gallery/${image.imageKey}/image/download`}
            alt={image.title || "image"}
          />
         : null}
         <p className='text'>{image.description}</p>
         <br/>
      </div>
    ))}
  </div>
  ); 
}

function MyDropzone({onUploaded, title, description}) {

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

    if(title) {
      formData.append("title", title);
    }
    if(description) {
      formData.append("description", description);
    }

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
  }, [onUploaded, title, description]);
  const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})


  return (
  <div {...getRootProps()} style={{
    border: '2px dashed #ccc',
    borderRadius: '4px',
    padding: '20px',
    margin: '100px 0 0 0',
    textAlign: 'center',
    cursor: 'pointer',
    backgroundColor: isDragActive ? '' : ''
  }}>
    <input {...getInputProps()} />
    {
      isDragActive ?
        <p>Drop the image here ...</p> :
        <p>Drag 'n' drop profile image, or click to select profile image</p>
    }
  </div>
  )
} 


const NavBar = ({show, onUpload}) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  
  const uploadClear = () => {
    //clear form after upload
    setTitle('');
    setDescription('');
    if(typeof onUpload === 'function') {
      onUpload();
    }
  }
  return (
    <div className={show ? 'sideNav active' : 'sideNav'}>
      <ul>
         <br/>
         <form>
          <label htmlFor='titleInput'>Title:</label>
          <input className="title"
                 id='titleInput' 
                 type="text" 
                 placeholder='Add a title...'
                 value={title}
                 onChange={(e) => setTitle(e.target.value)}
                 />
          <br/>
          <label htmlFor='descriptionInput'>Description:</label>
          <input id='descriptionInput' 
                 type="text" 
                 placeholder='Add a description...'
                 value={description}
                 onChange={(e) => setDescription(e.target.value)}
                 />
         </form>
         <MyDropzone classname='dropzone' onUploaded={uploadClear} title={title} description={description} />
      </ul>
    </div>
  )
}
  
function App() {
  const [showNav, setShowNav] = useState(false);
  return (
      <div className="App">
        <header>
          <button className="uploadButton" onClick={() => setShowNav(!showNav)}>upload</button>
          <NavBar show={showNav} />
        </header>
        <div>
        <Images />
        </div>
      </div>
  )
}

export default App
