export type Role = 'STUDENT' | 'PROFESSOR' | 'ADMIN'
export type Session = { token: string; userId: number; firstName: string; lastName: string; email: string; role: Role }
export type UserAccount = { id:number; firstName:string; lastName:string; email:string; role:Role }
export type Course = { id: number; name: string; description: string; ects:number; faculty:string; department:string; capacity:number; enrolled:number; enrolledByCurrentUser:boolean; professorId: number; professorName: string }
export type Assignment = { id:number; courseId:number; title:string; description:string; deadline:string; maxPoints:number; priority:'URGENT'|'SOON'|'OK' }
export type Material = { id:number;courseId:number;title:string;filename:string;contentType:string;fileSize:number;uploadedAt:string }
export type Prerequisite = { courseId:number;prerequisiteCourseId:number;prerequisiteCourseName:string }
export type Schedule = { id:number;courseId:number;dayOfWeek:string;startTime:string;endTime:string;room:string }
export type WeeklySchedule = Schedule & {courseName:string}
export type Notification = {id:number;type:string;title:string;message:string;read:boolean;createdAt:string}
export type NotificationSummary = {unread:number;items:Notification[]}
export type Review = {id:number;courseId:number;studentId:number;studentName:string;rating:number;comment:string;createdAt:string}
export type Recommendation = {course:Course;sharedStudents:number}
export type CourseDetail = { course:Course; assignments:Assignment[];materials:Material[];prerequisites:Prerequisite[];schedule:Schedule[];reviews:Review[];averageRating:number|null;reviewCount:number }
export type CourseProgress = { courseId: number; courseName: string; completed: number; total: number }
export type DashboardAssignment = { id: number; courseId: number; courseName: string; title: string; deadline: string; priority: 'URGENT'|'SOON'|'OK'; completed: boolean }
export type Dashboard = { courses: CourseProgress[]; upcomingDeadlines: DashboardAssignment[] }

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

export class ApiError extends Error { constructor(message: string, public status: number) { super(message) } }

async function request<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, { ...options, headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers } })
  if (!response.ok) {
    const body = await response.json().catch(() => null)
    if (token && response.status === 401) {
      window.dispatchEvent(new Event('unihub:session-expired'))
      throw new ApiError('Your session has expired. Please sign in again.', response.status)
    }
    throw new ApiError(typeof body?.details === 'string' ? body.details : body?.error ?? statusMessage(response.status), response.status)
  }
  if (response.status === 204) return undefined as T
  return response.json()
}

const statusMessage = (status: number) => status === 403 ? 'You do not have permission to perform this action.' : 'The request could not be processed.'

export const api = {
  login: (email: string, password: string) => request<Session>('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
  register: (data: { firstName:string; lastName:string; email:string; password:string }) => request<Session>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  dashboard: (token: string) => request<Dashboard>('/dashboard', {}, token),
  courses: (token: string, query = '') => request<Course[]>(`/courses${query ? `?${query}` : ''}`, {}, token),
  course: (id:number, token:string) => request<CourseDetail>(`/courses/${id}`, {}, token),
  enroll: (courseId: number, token: string) => request(`/courses/${courseId}/enroll`, { method: 'POST' }, token),
  createCourse: (data: {name:string;description:string;ects:number;faculty:string;department:string;capacity:number}, token:string) => request<Course>('/courses', { method:'POST', body:JSON.stringify(data) }, token),
  createAssignment: (courseId:number,data:{title:string;description:string;deadline:string;maxPoints:number},token:string) => request<Assignment>(`/courses/${courseId}/assignments`,{method:'POST',body:JSON.stringify(data)},token),
  complete: (id:number, completed:boolean, token:string) => request(`/assignments/${id}/completion`, { method: completed ? 'PUT' : 'DELETE' }, token)
  ,uploadMaterial: async (courseId:number,title:string,file:File,token:string) => {
    const form=new FormData();form.append('title',title);form.append('file',file)
    const response=await fetch(`${API_URL}/courses/${courseId}/materials`,{method:'POST',headers:{Authorization:`Bearer ${token}`},body:form})
    if(!response.ok)throw new ApiError((await response.json().catch(()=>null))?.details??'The material could not be uploaded.',response.status)
    return response.json() as Promise<Material>
  },
  downloadMaterial: async (material:Material,token:string) => downloadFile(`/materials/${material.id}/download`,material.filename,token),
  exportCalendar: (token:string) => downloadFile('/calendar/export.ics','unihub-calendar.ics',token),
  addPrerequisite: (courseId:number,requiredId:number,token:string) => request<Prerequisite>(`/courses/${courseId}/prerequisites/${requiredId}`,{method:'POST'},token),
  removePrerequisite: (courseId:number,requiredId:number,token:string) => request(`/courses/${courseId}/prerequisites/${requiredId}`,{method:'DELETE'},token),
  addSchedule: (courseId:number,data:{dayOfWeek:string;startTime:string;endTime:string;room:string},token:string) => request<Schedule>(`/courses/${courseId}/schedule`,{method:'POST',body:JSON.stringify(data)},token)
  ,schedule: (token:string) => request<WeeklySchedule[]>('/schedule',{},token)
  ,notifications: (token:string) => request<NotificationSummary>('/notifications',{},token)
  ,readNotification: (id:number,token:string) => request(`/notifications/${id}/read`,{method:'PATCH'},token)
  ,readAllNotifications: (token:string) => request('/notifications/read-all',{method:'PATCH'},token)
  ,review: (courseId:number,rating:number,comment:string,token:string) => request(`/courses/${courseId}/reviews`,{method:'POST',body:JSON.stringify({rating,comment})},token)
  ,recommendations: (token:string) => request<Recommendation[]>('/recommendations',{},token)
  ,users: (token:string) => request<UserAccount[]>('/admin/users',{},token)
  ,changeUserRole: (userId:number,role:Role,token:string) => request<UserAccount>(`/admin/users/${userId}/role`,{method:'PATCH',body:JSON.stringify({role})},token)
}

async function downloadFile(path:string,filename:string,token:string){const response=await fetch(`${API_URL}${path}`,{headers:{Authorization:`Bearer ${token}`}});if(!response.ok)throw new ApiError('The file could not be downloaded.',response.status);const url=URL.createObjectURL(await response.blob());const link=document.createElement('a');link.href=url;link.download=filename;link.click();URL.revokeObjectURL(url)}
