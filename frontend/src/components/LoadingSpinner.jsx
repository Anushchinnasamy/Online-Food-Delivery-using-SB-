const LoadingSpinner = ({ size = 'md', fullScreen = false }) => {
  const sizeClasses = {
    sm: 'w-6 h-6',
    md: 'w-12 h-12',
    lg: 'w-16 h-16',
    xl: 'w-24 h-24',
  };

  const spinner = (
    <div className="relative">
      <div className={`${sizeClasses[size]} border-4 border-purple-200 border-t-purple-600 rounded-full animate-spin glow`}></div>
      <div className={`${sizeClasses[size]} border-4 border-pink-200 border-t-pink-600 rounded-full animate-spin absolute top-0 left-0`} style={{ animationDirection: 'reverse', animationDuration: '1s' }}></div>
    </div>
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 glass-panel flex items-center justify-center z-50">
        <div className="text-center">
          {spinner}
          <p className="mt-4 text-white font-semibold text-lg">Loading...</p>
        </div>
      </div>
    );
  }

  return <div className="flex justify-center items-center p-8">{spinner}</div>;
};

export default LoadingSpinner;
