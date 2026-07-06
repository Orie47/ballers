function Toast({ message, onDismiss }) {
  return (
    <div className="fixed bottom-6 left-1/2 z-50 flex -translate-x-1/2 items-center gap-3 rounded-lg bg-(--text-h) px-4 py-2.5 text-sm text-(--bg) shadow-lg">
      <span>{message}</span>
      <button
        type="button"
        onClick={onDismiss}
        aria-label="Dismiss"
        className="opacity-70 hover:opacity-100"
      >
        ✕
      </button>
    </div>
  )
}

export default Toast
