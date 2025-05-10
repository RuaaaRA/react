import { useState } from 'react';
import { Dialog, Transition } from '@headlessui/react';
import { Fragment } from 'react';
import { CheckIcon, XMarkIcon } from '@heroicons/react/24/outline';

const AssignInstructorModal = ({ isOpen, setIsOpen, onAssign, course, instructors, onClose }) => {
  const [selectedInstructor, setSelectedInstructor] = useState('');

  const handleAssign = () => {
    if (!selectedInstructor) return;
    onAssign(course.id, selectedInstructor);
    setSelectedInstructor('');
  };

  return (
    <Transition appear show={isOpen} as={Fragment}>
      <Dialog as="div" className="relative z-10" onClose={() => {
        setIsOpen(false);
        onClose();
      }}>
        <Transition.Child
          as={Fragment}
          enter="ease-out duration-300"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-200"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-black bg-opacity-25" />
        </Transition.Child>

        <div className="fixed inset-0 overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4 text-center">
            <Transition.Child
              as={Fragment}
              enter="ease-out duration-300"
              enterFrom="opacity-0 scale-95"
              enterTo="opacity-100 scale-100"
              leave="ease-in duration-200"
              leaveFrom="opacity-100 scale-100"
              leaveTo="opacity-0 scale-95"
            >
              <Dialog.Panel className="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all">
                <div className="flex justify-between items-center">
                  <Dialog.Title
                    as="h3"
                    className="text-lg font-medium leading-6 text-gray-900"
                  >
                    Assign Instructor
                  </Dialog.Title>
                  <button
                    onClick={() => {
                      setIsOpen(false);
                      onClose();
                    }}
                    className="p-1 rounded-full hover:bg-gray-100"
                  >
                    <XMarkIcon className="h-5 w-5 text-gray-500" />
                  </button>
                </div>

                <div className="mt-4">
                  <p className="text-sm text-gray-500 mb-4">
                    Assign an instructor to <span className="font-semibold">{course?.title}</span>
                  </p>

                  <div className="mb-4">
                    <label htmlFor="instructor" className="block text-sm font-medium text-gray-700 mb-1">
                      Select Instructor
                    </label>
                    <select
                      id="instructor"
                      className="w-full border border-gray-300 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-purple-500"
                      value={selectedInstructor}
                      onChange={(e) => setSelectedInstructor(e.target.value)}
                    >
                      <option value="">Select an instructor</option>
                      {instructors.map((instructor) => (
                        <option key={instructor.id} value={instructor.id}>
                          {instructor.firstName} {instructor.lastName}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="mt-6 flex justify-end space-x-3">
                  <button
                    type="button"
                    className="inline-flex justify-center rounded-md border border-transparent bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-gray-500"
                    onClick={() => {
                      setIsOpen(false);
                      onClose();
                    }}
                  >
                    Cancel
                  </button>
                  <button
                    type="button"
                    className="inline-flex justify-center rounded-md border border-transparent bg-purple-600 px-4 py-2 text-sm font-medium text-white hover:bg-purple-700 focus:outline-none focus-visible:ring-2 focus-visible:ring-purple-500 disabled:opacity-50"
                    onClick={handleAssign}
                    disabled={!selectedInstructor}
                  >
                    Assign
                  </button>
                </div>
              </Dialog.Panel>
            </Transition.Child>
          </div>
        </div>
      </Dialog>
    </Transition>
  );
};

export default AssignInstructorModal;