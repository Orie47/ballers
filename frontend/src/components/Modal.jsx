function Modal({ title, onClose, children }) {
  return (
    <div
      onClick={onClose}
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
    >
      <div
        onClick={(event) => event.stopPropagation()}
        className="w-full max-w-sm rounded-lg bg-(--bg) p-5 shadow-lg"
      >
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-(--text-h)">{title}</h2>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close"
            className="text-(--text) hover:text-(--text-h)"
          >
            ✕
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}

export default Modal
