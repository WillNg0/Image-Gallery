import { useState, useEffect, useCallback } from 'react'
import './App.css'
import axios from 'axios'
import {useDropzone} from 'react-dropzone'

const ImageCard = ({image, onUpdate, onDelete}) => {
  const [isHovered, setIsHovered] = useState(false);
  const [isEditingTitle, setIsEditingTitle] = useState(false);
  const [isEditingDescription, setIsEditingDescription] = useState(false);
  const [editTitle, setEditTitle] = useState(image.title || '');
  const [editDescription, setEditDescription] = useState(image.description || '');
  const [isDeleting, setIsDeleting] = useState(false);

  const handleSaveTitle = async () => {
    try {
      await axios.put(
        `http://localhost:8080/api/v1/image-gallery/${image.imageKey}/title`,
        null,
        { params: { title: editTitle } }
      );
      setIsEditingTitle(false);
      if (onUpdate) onUpdate();
    } catch (err) {
      console.error('Error updating title:', err);
      alert('Failed to update title');
    }
  };

  const handleSaveDescription = async () => {
    try {
      await axios.put(
        `http://localhost:8080/api/v1/image-gallery/${image.imageKey}/description`,
        null,
        { params: { description: editDescription } }
      );
      setIsEditingDescription(false);
      if (onUpdate) onUpdate();
    } catch (err) {
      console.error('Error updating description:', err);
      alert('Failed to update description');
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this image?')) {
      return;
    }
    
    setIsDeleting(true);
    try {
      await axios.delete(`http://localhost:8080/api/v1/image-gallery/${image.imageKey}`);
      if (onDelete) onDelete();
    } catch (err) {
      console.error('Error deleting image:', err);
      alert('Failed to delete image');
      setIsDeleting(false);
    }
  };

  return (
    <div 
      className="image-card"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      style={{
        position: 'relative',
        marginBottom: '20px',
        padding: '10px',
        border: '1px solid #ddd',
        borderRadius: '8px'
      }}
    >
      {isHovered && (
        <div style={{
          position: 'absolute',
          top: '10px',
          right: '10px',
          display: 'flex',
          gap: '5px',
          zIndex: 10
        }}>
          <button
            onClick={() => {
              setIsEditingTitle(true);
              setEditTitle(image.title || '');
            }}
            style={{
              padding: '5px 10px',
              backgroundColor: '#4CAF50',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Edit Title
          </button>
          <button
            onClick={() => {
              setIsEditingDescription(true);
              setEditDescription(image.description || '');
            }}
            style={{
              padding: '5px 10px',
              backgroundColor: '#2196F3',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Edit Desc
          </button>
          <button
            onClick={handleDelete}
            disabled={isDeleting}
            style={{
              padding: '5px 10px',
              backgroundColor: '#f44336',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: isDeleting ? 'not-allowed' : 'pointer',
              opacity: isDeleting ? 0.6 : 1
            }}
          >
            {isDeleting ? 'Deleting...' : 'Delete'}
          </button>
        </div>
      )}
      
      {isEditingTitle ? (
        <div style={{ marginBottom: '10px' }}>
          <input
            type="text"
            value={editTitle}
            onChange={(e) => setEditTitle(e.target.value)}
            onBlur={handleSaveTitle}
            onKeyUp={(e) => {
              if (e.key === 'Enter') handleSaveTitle();
              if (e.key === 'Escape') {
                setIsEditingTitle(false);
                setEditTitle(image.title || '');
              }
            }}
            autoFocus
            style={{ width: '100%', padding: '5px', fontSize: '1.2em', fontWeight: 'bold' }}
          />
        </div>
      ) : (
        <h1 className='text' onClick={() => setIsEditingTitle(true)} style={{ cursor: 'pointer' }}>
          {image.title || 'untitled'}
        </h1>
      )}
      
      {image.imageKey ? 
        <img
          src={`http://localhost:8080/api/v1/image-gallery/${image.imageKey}/image/download`}
          alt={image.title || "image"}
          style={{ width: '100%', maxWidth: '500px', height: 'auto' }}
        />
       : null}
       
      {isEditingDescription ? (
        <div style={{ marginTop: '10px' }}>
          <input
            type="text"
            value={editDescription}
            onChange={(e) => setEditDescription(e.target.value)}
            onBlur={handleSaveDescription}
            onKeyUp={(e) => {
              if (e.key === 'Enter') handleSaveDescription();
              if (e.key === 'Escape') {
                setIsEditingDescription(false);
                setEditDescription(image.description || '');
              }
            }}
            autoFocus
            style={{ width: '100%', padding: '5px' }}
          />
        </div>
      ) : (
        <p className='text' onClick={() => setIsEditingDescription(true)} style={{ cursor: 'pointer', marginTop: '10px' }}>
          {image.description || 'no description'}
        </p>
      )}
    </div>
  );
};

const Images = ({refreshTrigger}) => {

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
      <ImageCard
        key={image.imageKey || index}
        image={image}
        onUpdate={fetchImages}
        onDelete={fetchImages}
      />
    ))};
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
  const [refreshTrigger, setRefreshTrigger] = useState(0)

  const refresh = () => {
    setRefreshTrigger(prev => prev + 1)
  }

  return (
      <div className="App">
        <header>
          <button className="uploadButton" onClick={() => setShowNav(!showNav)}>upload</button>
          <NavBar show={showNav} onUpload={refresh}/>
        </header>
        <div>
        <Images refreshTrigger={refreshTrigger}/>
        </div>
      </div>
  )
}

export default App
